package cmd;
//Tenkaichi Scouter Detector by ViveTheJoestar
import java.io.File;
import java.io.FilenameFilter;

public class Main {
	public static boolean wiiMode;
	public static void main(String[] args) {
		try {
			String helpText = "USAGE: java -jar scouter-detect.jar [arg] \"path/to/folder\"";
			helpText += "\nValid Arguments:\n* -r -> Read PAK files and save their scouter information in a CSV.";
			helpText += "\n* -w -> Write fixes to all PAK files with invalid scouter information.";
			if (args.length > 0) {
				boolean read = args[0].equals("-r"), write = args[0].equals("-w");
				if (read || write) {
					if (args.length > 1) {
						File folder = new File(args[1].replace("\"", ""));
						if (folder.isDirectory()) {
							File[] pakFiles = folder.listFiles(new FilenameFilter() {
								@Override
								public boolean accept(File dir, String name) {
									String lower = name.toLowerCase();
									String[] arr = lower.split("_");
									//validate PAK files by checking their file names to see if they are costume PAKs
									boolean checkDmg = false, checkReg = arr[arr.length - 1].matches("\\dp.pak");
									if (arr.length > 2)
										checkDmg = arr[arr.length - 2].matches("\\dp") && arr[arr.length - 1].equals("dmg.pak");
									if (checkDmg || checkReg) return true;
									return false;
								}
							});
							if (pakFiles.length > 0) {
								long start = System.currentTimeMillis();
								CharaPak[] paks = new CharaPak[pakFiles.length];
								for (int i = 0; i < paks.length; i++) {
									paks[i] = new CharaPak(pakFiles[i]);
									if (!paks[i].isValid()) System.out.println("WARNING: Skipping " + paks[i].getName() + "...");
								}
								if (read) Scouter.writeSearchCsv(paks);
								else if (write) Scouter.fixInvalidParams(paks);
								long end = System.currentTimeMillis();
								System.out.printf("TIME: %.3f s\n", (end - start) / 1000.0);
							}
							else System.out.println("ERROR: No PAK files in provided directory!");
						}
					}
					else System.out.println("ERROR: No directory provided!");
				}
				else if (args[0].equals("-h")) System.out.println(helpText);
				else System.out.println("ERROR: Invalid argument!");
			}
			else System.out.println(helpText);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}