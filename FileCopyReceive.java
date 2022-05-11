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
public final class FileCopyReceive {

	/**
	 * Copies a file. The first argument is expected to be a qualified source file name, the second
	 * a qualified target file name.
	 * @param args the VM arguments
	 * @throws IOException if there's an I/O related problem
	 */
	static public void main (final String[] args) throws IOException {
		final Path destinationPath = Paths.get(args[0]);
		String ip = "127.0.0.1";
//		int port = 0;
		int port = 4711; // Test port
		
		if (args.length < 3) {
			if(args[1] != null || args[1] != "") {
				port = Integer.parseInt(args[1]);
			}
		}
		if (args.length == 3) {
			if(args[1] != null || args[1] != "") {
				ip = args[1];
			}
			if(args[2] != null || args[2] != "") {
				port = Integer.parseInt(args[2]);
			}
		}
		
		final String finIP = ip;
		final int finPort = port;
		
		final Path sinkPath = Paths.get(args[1]);
		if (sinkPath.getParent() != null && !Files.isDirectory(sinkPath.getParent())) throw new IllegalArgumentException(sinkPath.toString());

		// Files.copy(sourcePath, sinkPath, StandardCopyOption.REPLACE_EXISTING);
		// Client, IP & Port
		
		final byte[] buffer = new byte[0x10000];
		
		Runnable transporterOutput = () -> {
			try (OutputStream fos = Files.newOutputStream(sinkPath);
					Socket client = new Socket(finIP, finPort);){
				
				InputStream input = client.getInputStream();
				
				while(input.read() != -1) {
//					fos.write(input.read(buffer));
					fos.write(buffer, 0, input.read(buffer));
				}
				
				fos.close();
				input.close();
				System.out.println("done.");
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		};
		
		Thread t2 = new Thread(transporterOutput);
		
		t2.start();
	}
}
