package com.wavecell.sample.app.log

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.eght.voice.sdk.Voice
import com.wavecell.sample.app.models.SampleAppLog
import com.wavecell.sample.app.models.SampleAppLogLevel
import com.wavecell.sample.app.utils.DeviceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class FileLogWriter constructor(private val context: Context,
                                private val voice: Voice): LogWriter {

    companion object {
        private val TAG = AppTag.make(AppFeatureTag.SAMPLE_APPLICATION, AppComponentTag.LOG_DATA, FileLogWriter::class.java)
        private const val LOG_FILE_DATE_PATTERN = "MM-dd-yyyy_HH-mm-ss"
        private const val FILE_PREFIX = "wavecell_debug_"
        private const val THREAD_NAME = "FileLogging"
        const val LOG_FILENAME = "current_wavecell_log.log"
        const val ARCHIVED_PATTERN = "archive_collected_on"
        private const val LOG_FILE_SIZE = 2 * 1024 * 1024L //2MB uncompressed current log
        private const val ARCHIVES_TO_KEEP = 5 //keep a number of archives before deleting them
    }


    private val timestampFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)
    private val fileOutputLock = Any()
    private var currentLogFile: File
    private val loggingThreadHandler: Handler
    private var fileOutputStream: FileOutputStream?

    init {
        val loggingThread = HandlerThread(THREAD_NAME)
        loggingThread.start()
        loggingThreadHandler = Handler(loggingThread.looper)

        val logsDirectory = File(context.cacheDir, "logs")
        currentLogFile = File(logsDirectory, LOG_FILENAME)
        fileOutputStream = createLogFile(currentLogFile)
    }


    /*
    Main zip file provided to user
     */

    suspend fun zipFile(): FileResult? {
        return withContext(Dispatchers.IO) {
            try {
                createZipFile()
            } catch (e: Exception) {
                AppLog.e(TAG, "Error creating log file", e)
                null
            }
        }
    }

    /*
    Read-only Properties
     */

    private val zipFilename: String
        get() {
            val time = Calendar.getInstance().time
            return StringBuilder(FILE_PREFIX)
                    .append(SimpleDateFormat("MMddyyyy_HHmmss", Locale.US).format(time))
                    .append(".zip")
                    .toString()
        }


    /*
    Log Writer Implementation
     */

    override fun log(log: SampleAppLog) {
        loggingThreadHandler.post(TextLoggingRunnable(log))
    }


    override fun cleanup() {
        synchronized(fileOutputLock) {
            try {
                //dumping all cached data to file. Do this on the app main thread, since it's going to die anyway
                fileOutputStream?.channel?.force(true)
            } catch (e: IOException) {
                //Suppress. This is called only when the process dies.
            }
        }
    }


    /*
    Helpers
     */

    @Throws(FilePermissionException::class)
    private fun getUserLogsDirectory(context: Context): File {
        val filesDir = context.filesDir.path
        val userFeedbackLogDir = File("$filesDir/UserLogs/")
        return try {
            if (userFeedbackLogDir.exists() && userFeedbackLogDir.isDirectory || userFeedbackLogDir.mkdir()) {
                userFeedbackLogDir
            } else {
                throw FilePermissionException("Cannot read user feedback logs")
            }
        } catch (se: SecurityException) {
            throw FilePermissionException("cannot read user feedback logs due to security setting", se)
        }
    }

    @Throws(IOException::class)
    private fun createLogFile(currentLogFile: File): FileOutputStream {
        synchronized(fileOutputLock) {

            //create the directory structure if needed
            currentLogFile.parentFile?.mkdirs()
            return FileOutputStream(currentLogFile, true)
        }
    }

    @Throws(IOException::class)
    private fun addLogsToZipFile(zipOutputStream: ZipOutputStream) {
        val logFileDateFormat = SimpleDateFormat(LOG_FILE_DATE_PATTERN, Locale.getDefault())
        val logFileName = "_Wavecell_Log_" + logFileDateFormat.format(Calendar.getInstance().time) + ".log"
        val zipEntry = ZipEntry(logFileName)
        zipOutputStream.putNextEntry(zipEntry)
        writeAllLogsToArchive(zipOutputStream)
    }

    @Throws(IOException::class)
    private fun writeAllLogsToArchive(zipOutputStream: ZipOutputStream) {
        FileLoggingExtractor.extractLogsToFileStream(context, zipOutputStream)
    }

    private fun logDeviceInfo() {
        val application = "Wavecell Sample App"
        val applicationId = DeviceUtils.APPLICATION_ID
        val className = this.javaClass.simpleName
        val timestamp = System.currentTimeMillis()
        val logLevel = SampleAppLogLevel.INFO
        val thread = Thread.currentThread().name
        val featureTag = "Wavecell SDK"
        val deviceOS = DeviceUtils.OPERATING_SYSTEM
        val deviceModel = DeviceUtils.DEVICE_MODEL
        val appVersion = DeviceUtils.APP_VERSION
        val sdkVersion = voice.sdkVersion
        val componentsversion = voice.componentsVersion


        val message = StringBuilder()
                .append("\tapplication: $application\n")
                .append("\tapplication_id: $applicationId\n")
                .append("\tdevice_os: $deviceOS\n")
                .append("\tdevice_model: $deviceModel\n")
                .append("\tapp_version: $appVersion\n")
                .append("\tsdk_version: $sdkVersion\n")
                .append("\tcomponents_version: $componentsversion\n")
                .append(voice.registrationLog)
                .toString()

        val wavecellLog = SampleAppLog(className, timestamp, logLevel, thread, featureTag, message)
        log(wavecellLog)
    }


    private fun createZipFile(): FileResult {
        logDeviceInfo()
        val result = FileResult()
        val userLogsDirectory = getUserLogsDirectory(context)
        val zipFile = File(userLogsDirectory, zipFilename)

        try {
            FileOutputStream(zipFile).use { fileOutputStream ->
                BufferedOutputStream(fileOutputStream).use { bufferedOutputStream ->
                    ZipOutputStream(bufferedOutputStream).use { zipOutputStream ->
                        var gotLogData = false
                        try {
                            gotLogData = true
                            result.callPacketDataAdded = true
                        } catch (e: IOException) {
                            // see comment in the addLogsToZipFile catch
                            AppLog.w(TAG, "Could not open call packet logs", e)
                        }
                        try {
                            addLogsToZipFile(zipOutputStream)
                            gotLogData = true
                        } catch (e: IOException) {
                            // As a wise man put it: any logs is better than no logs.
                            // This is non-fatal if we get any logs at all. Will throw later if we get 0 logs
                            AppLog.w(TAG, "Could not open text logs", e)
                            result.wasCompletelySuccessful = false
                        }
                        zipOutputStream.closeEntry()
                        if (!gotLogData) {
                            throw IOException("Could not get any log data at all")
                        }
                    }
                }
            }
        } catch (e: IOException) {
            throw IOException("Error trying to dump log", e)
        }

        result.file = zipFile
        return result
    }

    private fun generateTimestampInfo(timestamp: Long): String? {
        return timestampFormatter.format(Date(timestamp))
    }

    private fun getDataToLog(log: SampleAppLog): String {
        val logLine = StringBuilder()
        logLine.append(generateTimestampInfo(log.timestamp))
                .append(" [").append(log.logLevel)
                .append("] [").append(log.logTagInfo)
                .append("] [").append(log.logClassName)
                .append("]")
        log.logThreadInfo?.let {
            logLine.append(" ").append(log.logThreadInfo)
        }
        logLine.append(" ").append(log.logMessage)
        log.throwable?.let {
            logLine.append(" ").append(Log.getStackTraceString(log.throwable))
        }
        logLine.append("\n")
        return logLine.toString()
    }

    private fun cleanupExcessiveArchivedLogs() {
        currentLogFile.parentFile?.listFiles(StartsWithFilenameFilter(ARCHIVED_PATTERN))?.let { files ->
            Arrays.sort(files) //sorting lexicographically. A file towards the end of the array means is more recent
            val archiveCountThatNeedsDeletion: Int = files.size - ARCHIVES_TO_KEEP
            if (archiveCountThatNeedsDeletion > 0) {
                for (i in 0 until archiveCountThatNeedsDeletion) {
                    //traversing the files, deleting as many as needed, from the start (older)
                    files[i].delete()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun archiveCurrentLogs() {
        synchronized(fileOutputLock) {
            try {
                //flushing the current log file
                fileOutputStream!!.channel.force(true)
                val archiveName = String.format(Locale.US, "%s%d.zip", ARCHIVED_PATTERN,
                        System.currentTimeMillis())
                val zipFile = File(currentLogFile.parent, archiveName)
                FileOutputStream(zipFile).use { fileOutputStream ->
                    BufferedOutputStream(fileOutputStream).use { bufferedOutputStream ->
                        ZipOutputStream(bufferedOutputStream).use { zipOutputStream ->
                            FileInputStream(currentLogFile).use { currentLogFileStream ->
                                val zipEntry = ZipEntry("archived_logs.txt")
                                zipOutputStream.putNextEntry(zipEntry)
                                IOUtils.copy(currentLogFileStream, zipOutputStream)
                                zipOutputStream.closeEntry()
                            }
                        }
                    }
                }
            } finally {
                cleanupExcessiveArchivedLogs()
            }
        }
    }

    @Throws(IOException::class)
    private fun exceedsSize(): Boolean {
        synchronized(fileOutputLock) {
            return fileOutputStream!!.channel.size() > LOG_FILE_SIZE
        }
    }

    @Throws(IOException::class)
    private fun deleteCurrentLogFile() {
        synchronized(fileOutputLock) {
            fileOutputStream!!.close()
            currentLogFile.delete()
        }
    }


    /*
    Runnable
     */

    private inner class TextLoggingRunnable(private val log: SampleAppLog): Runnable {

        override fun run() {
            synchronized(fileOutputLock) {
                try {
                    fileOutputStream?.write(getDataToLog(log).toByteArray())
                    if (exceedsSize()) {
                        archiveCurrentLogs()
                        deleteCurrentLogFile()
                        fileOutputStream = createLogFile(currentLogFile)
                    }
                    Unit
                } catch (e: IOException) {
                    //print this in the console, since we cannot write it to file.
                    Log.w(THREAD_NAME, e)
                }
            }
        }
    }
}

