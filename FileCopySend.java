package edu.sb.ds.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import edu.sb.ds.util.Copyright;


/**
 * Demonstrates copying a file using a single thread. Note that this class is declared final because
 * it provides an application entry point, and therefore not supposed to be extended.
 */
@Copyright(year=2008, holders="Sascha Baumeister")
public final class FileCopySend {

	/**
	 * Copies a file. The first argument is expected to be a qualified source file name, the second
	 * a qualified target file name.
	 * @param args the VM arguments
	 * @throws IOException if there's an I/O related problem
	 */
	static public void main (final String[] args) throws IOException {
		final Path sourcePath = Paths.get(args[0]);
		int port = 0;
		
		if (!Files.isReadable(sourcePath)) throw new IllegalArgumentException(sourcePath.toString());
		if(args[1] != null || args[1] != "") {
			port = Integer.parseInt(args[1]);
		}
		
		final int finPort = port;
		
	//	Files.copy(sourcePath, sinkPath, StandardCopyOption.REPLACE_EXISTING);
		// Server, Port only
		
		
//		final PipedOutputStream output = new PipedOutputStream();
		
		final byte[] buffer = new byte[0x10000];
		
		Runnable transporterInput = () -> {
			try (InputStream fis = Files.newInputStream(sourcePath);
					ServerSocket service = new ServerSocket(finPort);) {
				
				Socket connection = service.accept();
				OutputStream output = connection.getOutputStream();
				
				while (fis.read() != -1) {
//					output.write(fis.read(buffer));
					output.write(buffer, 0, fis.read(buffer));
				}
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		};
		
		Thread t1 = new Thread(transporterInput);
		
		t1.start();
	}
}