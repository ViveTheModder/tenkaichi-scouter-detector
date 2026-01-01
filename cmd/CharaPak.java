package cmd;
//Tenkaichi Scouter Detector by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CharaPak {
	private byte pakState;
	private RandomAccessFile pak;
	private String name;
	
	private static final byte BT2_PAK = 0, BT3_PAK = 1, INVALID_PAK = -1;
	//Values provided by jagger1407
	private static final String[] Z_SEARCH_TYPES = {
		"Ki Search", "Ki Search (Fast)", "Scouter Search",
		"Scouter Search (Fast)", "Scouter Search (Very Fast)", "Ki Search (No Radar Flash)",
		"Ki Search (Androids)", "Ki Search (Slow & No Radar Flash)",
		"Ki Search (Villains)", "Ki Search (Mecha Frieza)"
	};
	
	public CharaPak(File f) throws IOException {
		pak = new RandomAccessFile(f, "rw");
		name = f.getName();
		int input = pak.readInt();
		//For context, a positive Little Endian integer is often equivalent to a negative Big Endian integer
		if (input > 0) Main.wiiMode = true;
		else Main.wiiMode = false;
		int numPakContents = LittleEndian.getInt(input);
		if (numPakContents == 250) pakState = BT2_PAK;
		else if (numPakContents == 252) pakState = BT3_PAK;
		else pakState = INVALID_PAK;
	}
	
	public boolean hasAlphaMdl() {
		try {
			pak.seek(12);
			int mdlAddr = LittleEndian.getInt(pak.readInt());
			if (pakState == BT2_PAK) mdlAddr += 44;
			else mdlAddr += 84;
			pak.seek(mdlAddr);
			byte alphaTexId = pak.readByte();
			/* A more accurate approach would be to search the contents of the MDL for 0x330000000000 (model part ID plus padding). 
			 * Though it is present in every model, the file size would also be checked if different from 64 bytes (empty model part). 
			 * And since most models do not exceed 300 KiB, reading just one byte instead translates to better performance.
			 * An alpha texture ID of zero either implies no scouter is present (likely) or its texture is the very first (unlikely).
			 * This approach will lead to false positives if modders fail to zero out the alpha texture ID after removing the model part. */
			if (alphaTexId != 0) return true;
			return false;
		} 
		catch (IOException e) {
			return false;
		}
	}
	public boolean isValid() {
		return pakState != -1;
	}
	public boolean setSearchVals(int searchVal) {
		try {
			int numSearchVals = 4;
			if (pakState == BT2_PAK) numSearchVals = 6;
			pak.seek(72);
			int cmnPrmAddr = LittleEndian.getInt(pak.readInt());
			if (pakState == BT2_PAK) cmnPrmAddr += 41;
			else cmnPrmAddr += 88;
			pak.seek(cmnPrmAddr);
			for (int i = 0; i < numSearchVals; i++) pak.writeByte(searchVal);
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}
	public byte[] getSearchVals() {
		try {
			byte[] searchVals;
			if (pakState == BT2_PAK) searchVals = new byte[6];
			else searchVals = new byte[4];
			pak.seek(72);
			int cmnPrmAddr = LittleEndian.getInt(pak.readInt());
			if (pakState == BT2_PAK) cmnPrmAddr += 41;
			else cmnPrmAddr += 88;
			pak.seek(cmnPrmAddr);
			pak.read(searchVals);
			return searchVals;
		}
		catch (IOException e) {
			return new byte[6];
		}
	}
	public String getSearchTypes(byte[] searchVals) {
		String searchTypes = "";
		for (int i = 0; i < searchVals.length; i++) {
			if (searchVals[i] >= Z_SEARCH_TYPES.length || searchVals[i] < 0)
				searchTypes += "Invalid (" + searchVals[i] + "),";
			else searchTypes += Z_SEARCH_TYPES[searchVals[i]] + ",";
		}
		return searchTypes;
	}
	public String getName() {
		return name;
	}
}