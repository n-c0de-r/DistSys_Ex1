package edu.sb.ds.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import edu.sb.ds.util.Copyright;


/**
 * Demonstrates copying a file using a single thread. Note that this class is declared final because
 * it provides an application entry point, and therefore not supposed to be extended.
 */
@Copyright(year=2008, holders="Sascha Baumeister")
public final class FileCopyMultiThreaded {

	/**
	 * Copies a file. The first argument is expected to be a qualified source file name, the second
	 * a qualified target file name.
	 * @param args the VM arguments
	 * @throws IOException if there's an I/O related problem
	 */
	static public void main (final String[] args) throws IOException {
		final Path sourcePath = Paths.get(args[0]);
		if (!Files.isReadable(sourcePath)) throw new IllegalArgumentException(sourcePath.toString());

		final Path sinkPath = Paths.get(args[1]);
		if (sinkPath.getParent() != null && !Files.isDirectory(sinkPath.getParent())) throw new IllegalArgumentException(sinkPath.toString());

		// Files.copy(sourcePath, sinkPath, StandardCopyOption.REPLACE_EXISTING);
		final PipedInputStream input = new PipedInputStream(1024*1024); // 1MB Minimum Entkopplung
		final PipedOutputStream output = new PipedOutputStream(input);
		
		final Runnable transporterInput = () -> {
			try (OutputStream os = output) {
				Files.copy(sourcePath, os);
			} catch (IOException e) {
				throw new UncheckedIOException(e); // Checked vs Unchecked Exceptions lesen
			}
		};
		
		final Runnable transporterOutput = () -> {
			try (InputStream is = input) {
				Files.copy(is, sinkPath);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
		
		final Thread t1 = new Thread(transporterInput, "thread-read");
		final Thread t2 = new Thread(transporterOutput, "thread-write");
		
		t1.start();
		t2.start();
	}
}
