import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class fileProcessing {

	// takes a file and returns a string with the whole file
	public String readAllBytesJava7(String filePath) {
		String content = "";

		try {
			content = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

	public String[] fileToArray2(String filename) throws IOException {

		// takes a file, reads it line by line, and returns an array with each line
		InputStream is = createInput(filename);

		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		String lines[] = new String[100];
		int lineCount = 0;
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (lineCount == lines.length) {
				String temp[] = new String[lineCount + 100];
				System.arraycopy(lines, 0, temp, 0, (lineCount));
				lines = temp;
			}
			lines[lineCount++] = line;
		}
		reader.close();

		if (lineCount == lines.length) {
			return lines;
		}

		// correct the size size
		String output[] = new String[lineCount];
		System.arraycopy(lines, 0, output, 0, lineCount);
		return output;

	}

	public InputStream createInput(String filename) throws FileNotFoundException {
		File file = new File(filename);
		return new FileInputStream(file);

	}

}
