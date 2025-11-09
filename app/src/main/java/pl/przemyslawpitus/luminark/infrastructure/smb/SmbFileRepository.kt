package pl.przemyslawpitus.luminark.infrastructure.smb

import android.net.Uri
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2CreateOptions
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.mssmb2.SMBApiException
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.connection.Connection
import com.hierynomus.smbj.session.Session
import com.hierynomus.smbj.share.DiskShare
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.przemyslawpitus.luminark.domain.DirectoryEntry
import pl.przemyslawpitus.luminark.domain.FileRepository
import pl.przemyslawpitus.luminark.domain.FilesLister
import timber.log.Timber
import java.io.InputStream
import java.nio.file.Path
import java.util.EnumSet
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.path.pathString

const val HOSTNAME = "<IP ADDRESS>"
const val SHARE_NAME = "<SHARE>"
const val USER = "<USER>"
const val PASSWORD = "<PASSWORD>"
const val DOMAIN = "<DOMAIN>"

private val IGNORED_FOLDERS = setOf(".", "..", "#recycle", "\$RECYCLE.BIN", "System Volume Information")

@Singleton
class SmbFileRepository @Inject constructor() : FilesLister, FileRepository {

    private val client = SMBClient()
    private var session: Session? = null
    private var connection: Connection? = null
    private var diskShare: DiskShare? = null

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            Timber.d("Shutdown hook triggered: Disconnecting from SMB share...")
            disconnect()
            Timber.d("SMB Client closed.")
        })
    }

    fun getBasePath(): Uri = Uri.Builder()
        .scheme("smb")
        .authority(DOMAIN)
        .appendPath(SHARE_NAME)
        .build()

    suspend fun connectToShare() {
        withContext(Dispatchers.IO) {
            connection = client.connect(HOSTNAME)
            val authContext = AuthenticationContext(USER, PASSWORD.toCharArray(), DOMAIN)
            session = connection!!.authenticate(authContext)
            diskShare = session!!.connectShare(SHARE_NAME) as? DiskShare
        }
    }

    override fun listFilesAndDirectories(directoryAbsolutePath: Path): List<DirectoryEntry> {
        return try {
            diskShare!!.list(directoryAbsolutePath.pathString)
                .filter { it.fileName !in IGNORED_FOLDERS }
                .map { it.toSmbDirectoryEntry(directoryAbsolutePath) }
        } catch (exception: SMBApiException) {
            Timber.w(exception, "Could not list path '%s'", directoryAbsolutePath)
            emptyList()
        }
    }

    override suspend fun <T> useReadFileStream(absolutePath: Path, block: (InputStream) -> T): T? =
        withContext(Dispatchers.IO) {
            val share = diskShare ?: return@withContext null

            if (!share.fileExists(absolutePath.pathString)) {
                Timber.w("File does not exist: %s", absolutePath)
                return@withContext null
            }
            val accessMask = EnumSet.of(AccessMask.FILE_READ_DATA)
            val shareAccess = EnumSet.of(SMB2ShareAccess.FILE_SHARE_READ)
            val createDisposition = SMB2CreateDisposition.FILE_OPEN
            val createOptions = EnumSet.of(SMB2CreateOptions.FILE_NON_DIRECTORY_FILE)

            try {
                share.openFile(
                    absolutePath.pathString,
                    accessMask,
                    EnumSet.noneOf(FileAttributes::class.java),
                    shareAccess,
                    createDisposition,
                    createOptions
                ).use { smbFile ->
                    smbFile.inputStream.use { inputStream ->
                        block(inputStream)
                    }
                }
            } catch (exception: Exception) {
                Timber.e(exception, "Error opening or reading file '%s'", absolutePath)
                null
            }
        }

    private fun disconnect() {
        diskShare?.close()
        session?.close()
        connection?.close()
    }

}

private fun FileIdBothDirectoryInformation.toSmbDirectoryEntry(
    absolutePath: Path,
): SmbDirectoryEntry {
    return SmbDirectoryEntry(
        smbInfo = this,
        absolutePath = absolutePath.resolve(this.fileName)
    )
}

class SmbDirectoryEntry(
    smbInfo: FileIdBothDirectoryInformation,
    override val absolutePath: Path,
) : DirectoryEntry {
    override val name: String = smbInfo.fileName
    override val isDirectory: Boolean = smbInfo.fileAttributes.and(FileAttributes.FILE_ATTRIBUTE_DIRECTORY.value) > 0
    override val isFile: Boolean = !isDirectory
}