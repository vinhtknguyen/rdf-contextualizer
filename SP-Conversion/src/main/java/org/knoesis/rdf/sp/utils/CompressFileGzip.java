package org.knoesis.rdf.sp.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressFileGzip {

	public void compress(String source, String des) {

		byte[] buffer = new byte[1024];

		try {

			FileOutputStream fileOutputStream = new FileOutputStream(des);

			GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fileOutputStream);

			FileInputStream fileInput = new FileInputStream(source);

			int bytes_read;

			while ((bytes_read = fileInput.read(buffer)) > 0) {
				gzipOuputStream.write(buffer, 0, bytes_read);
			}

			fileInput.close();

			gzipOuputStream.finish();
			gzipOuputStream.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void decompress(String src, String des) {

		byte[] buffer = new byte[1024];

		try {

			FileInputStream fileIn = new FileInputStream(src);

			GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);

			FileOutputStream fileOutputStream = new FileOutputStream(des);

			int bytes_read;

			while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {

				fileOutputStream.write(buffer, 0, bytes_read);
			}

			gZIPInputStream.close();
			fileOutputStream.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
