//https://docs.oracle.com/javase/tutorial/essential/io/notification.html
//https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java
package raceattscan;

import java.nio.file.*;
import java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

public class RaceAttScn
{
	WatchService watcher;
	WatchKey key;
	static Path dir = Paths.get("/tmp");
	static RaceDataHandle dataHNDL = new RaceDataHandle();
	static Thread th = new Thread(dataHNDL);


	RaceAttScn (Path local_dir ) throws IOException {
		try{
			this.watcher = FileSystems.getDefault().newWatchService();
		}catch(Exception e){
			//code to handle an Exception here
		}

		try{
			this.key = local_dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		}catch(Exception e){
			//code to handle an Exception here
		}
	}

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>)event;
	}

	void processEvents(){
		for (;;) {
			try{
				key = watcher.take();
			}catch(Exception e){
				//code to handle an Exception here
			}

			//for every event
			for (WatchEvent<?> event: key.pollEvents()) {

				WatchEvent.Kind<?> kind = event.kind();
				
				if (kind == StandardWatchEventKinds.OVERFLOW) {
				    continue;
				}

				// The filename is the
				// context of the event.
				WatchEvent<Path> ev = cast(event);
				Path filename = ev.context();

				jumpToNewThread (filename);

			}

			// Reset the key -- this step is critical if you want to
			// receive further watch events.  If the key is no longer valid,
			// the directory is inaccessible so exit the loop.
			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}

	}

	void jumpToNewThread (Path local_filename){
		dataHNDL.filename = local_filename.toString();
		dataHNDL.run(  );
	}

	public static void main ( String[] args ) throws IOException {
		//Open another thread
		th.start();

		new RaceAttScn ( dir ).processEvents();
	}

}

