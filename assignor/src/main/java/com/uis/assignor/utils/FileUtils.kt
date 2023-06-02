package com.uis.assignor.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer

object FileUtils {

    /** 删除文件目录下所有文件 */
    @JvmStatic fun removeFileDirectory(root: File){
        kotlin.runCatching {
            for (name in root.list()!!) {
                val file = File(root, name)
                if (file.isDirectory) {
                    removeFileDirectory(file)
                }
                file.delete()
            }
        }.exceptionOrNull()?.printStackTrace()
    }

    /** 复制文件 */
    @JvmStatic fun copyFile(res: File, des: File): Boolean {
        kotlin.runCatching {
            val channelRes = RandomAccessFile(res, "rwd").channel
            val channelDes = RandomAccessFile(des, "rwd").channel
            channelRes.transferTo(0, res.length(), channelDes)
            channelRes.close()
            channelDes.close()
            return true
        }.exceptionOrNull()?.printStackTrace()
        return false
    }

    /** 写文件 */
    @JvmStatic fun writeFile(file :File, data :ByteArray, append: Boolean = false) {
        kotlin.runCatching{
            val channel = RandomAccessFile(file, "rwd").channel
            channel.lock()
            if (append) {
                channel.position(file.length())
            }
            val size = data.size
            val byteBuffer = ByteBuffer.allocate(size)
            byteBuffer.put(data, 0, size)
            byteBuffer.flip()
            channel.write(byteBuffer)
            byteBuffer.clear()
            channel.close()
        }.exceptionOrNull()?.printStackTrace()
    }

    /** 写文件 */
    @JvmStatic fun writeFileOutput(file: File,data: ByteArray){
        kotlin.runCatching {
            val fileOut = FileOutputStream(file)
            fileOut.write(data)
            fileOut.flush()
            fileOut.close()
        }.exceptionOrNull()?.printStackTrace()
    }

    /** 读文件 */
    @JvmStatic fun readFile(file: File): ByteArray? {
        kotlin.runCatching{
            if (file.exists()) {
                val channel = RandomAccessFile(file, "rd").channel
                channel.lock()
                var len = file.length().toInt()
                val data = ByteArray(len)
                val byteBuffer = ByteBuffer.allocate(1024)
                var total = 0
                while (true) {
                    len = channel.read(byteBuffer)
                    if(len == -1) break
                    byteBuffer.flip()
                    byteBuffer.get(data, total, len)
                    total += len
                    byteBuffer.clear()
                }
                return data
            }
        } .exceptionOrNull()?.printStackTrace()
        return null
    }

    /** 读文件 */
    @JvmStatic fun readFileFast(file: File): ByteArray? {
        kotlin.runCatching{
            if (file.exists()) {
                val channel = RandomAccessFile(file, "rd").channel
                channel.lock()
                val len = file.length().toInt()
                val byteBuffer = ByteBuffer.allocate(len)
                val data = ByteArray(len)
                channel.read(byteBuffer)
                byteBuffer.flip()
                byteBuffer.get(data, 0, len)
                byteBuffer.clear()
                return data
            }
        } .exceptionOrNull()?.printStackTrace()
        return null
    }

    @JvmStatic fun readFileInput(file :File):ByteArray?{
        kotlin.runCatching {
            if(file.exists()) {
                val fileIn = FileInputStream(file)
                val bytes = ByteArray(file.length().toInt())
                fileIn.read(bytes)
                fileIn.close()
                return bytes
            }
        }.exceptionOrNull()?.printStackTrace()
        return null
    }
}