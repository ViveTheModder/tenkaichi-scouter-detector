package cmd;
//Tenkaichi Scouter Detector by ViveTheJoestar
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.filechooser.FileSystemView;

public class Scouter {
	private static final String[] HEADER_NAMES = {
		"pak_name", "search_type", "alpha_mdl", "valid_params"
	};
	
	public static void fixInvalidParams(CharaPak[] paks) throws IOException {
		for (CharaPak pak: paks) {
			//Originally, the program would read files it had previously skipped/warned the user about
			if (pak.isValid()) {
				String pakName = pak.getName();
				boolean validParams = false;
				boolean hasAlpha = pak.hasAlphaMdl();
				String types = pak.getSearchTypes(pak.getSearchVals());
				if (types.contains("Scouter") && hasAlpha) validParams = true;
				else if (types.contains("Ki") && !hasAlpha) validParams = true;
				if (!validParams) {
					boolean result;
					if (hasAlpha) result = pak.setSearchVals(2);
					else result = pak.setSearchVals(0);
					if (result) System.out.println("SUCCESS: Changed Z-Search values for " + pakName + "!");
					else System.out.println("WARNING: Skipping" + pakName + "...");
				}
			}
		}
	}
	public static void writeSearchCsv(CharaPak[] paks) throws IOException {
		boolean hasValidPak = false;
		File dir = FileSystemView.getFileSystemView().getDefaultDirectory();
		File csv = dir.toPath().resolve("z-search.csv").toFile();
		//Excel in particular will make Java throw a FileNotFoundException because of this 
		if (!csv.renameTo(csv)) {
			System.out.println("ERROR: CSV file cannot be overwritten (in use by another process).");
			return;
		}
		FileWriter fw = new FileWriter(csv);
		String header = HEADER_NAMES[0] + ",";
		for (int i = 1; i <= 6; i++) header += HEADER_NAMES[1] + "_" + i + ",";
		header += HEADER_NAMES[2] + "," + HEADER_NAMES[3];
		fw.write(header + "\n");
		for (CharaPak pak: paks) {
			//Originally, the program would read files it had previously skipped/warned the user about
			if (pak.isValid()) {
				hasValidPak = true;
				String pakName = pak.getName();
				System.out.println("PROGRESS: Reading " + pakName + "...");
				boolean validParams = false;
				boolean hasAlpha = pak.hasAlphaMdl();
				String types = pak.getSearchTypes(pak.getSearchVals());
				if (types.contains("Scouter") && hasAlpha) validParams = true;
				else if (types.contains("Ki") && !hasAlpha) validParams = true;
				String line = pakName + "," + types + hasAlpha + "," + validParams;
				fw.write(line + "\n");
			}
		}
		fw.close();
		if (!hasValidPak) csv.delete(); 
	}
}