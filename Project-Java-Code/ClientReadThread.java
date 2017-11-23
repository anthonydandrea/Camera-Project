
import java.io.IOException;
import java.io.InputStream;

// This thread has the double responsibility of connecting
// and reading data from the socket
public class ClientReadThread extends Thread {

	private ClientSharedData monitor;
	private byte[] buffer;

	public ClientReadThread(ClientSharedData mon) {
		monitor = mon;
		buffer = new byte[65496];
	}

	// Receive packages of random size from active connections.
	public void run() {
		while (!monitor.isShutdown())
		{
			System.out.println("Running client");
			try {
				// Wait for active connection
				System.out.println("Waiting for monitor to be active");

				monitor.waitUntilActive();

				InputStream is = monitor.getSocket().getInputStream();
				// Receive data packages of different sizes
				while (true) {
					// Read header
					System.out.println("Reading package");

					int size = Pack.HEAD_SIZE;
					int n = 0;
					while ((n = is.read(buffer, n, size)) > 0) {
						System.out.println("read...");
						size -= n;
					}
					if (size != 0) {
						System.out.println("size != 0, size = "+size);
						//break;
					}

					// Read payload
					int bufsize = size = Pack.unpackHeaderSize(buffer);
					n = 0;
					while ((n = is.read(buffer, n+Pack.HEAD_SIZE, size)) > 0) {
						String s = new String(buffer);
						System.out.println(s);
						size -= n;
					}
					if (size != 0) {
						System.out.println("size != 0, breaking");

						//break;
					}

					// Unpack payload and verify integrity
					//Utils.printBuffer("ClientReadThread", bufsize, buffer);
					Pack.unpackPayloadAndVerifyChecksum(buffer);
				}
			} catch (IOException e) {
				// Something happened with the connection
				//
				// Example: the connection is closed on the server side, but
				// the client is still trying to write data.
				monitor.setActive(false);
				Utils.println("No connection on client side");
			} catch (InterruptedException e) {
				// Occurs when interrupted
				monitor.shutdown();
				break;
			}
		}

		// No resources to dispose since this is the responsibility
		// of the shutdown thread.
		Utils.println("Exiting ClientReadThread");
	}
}
