package custom_utils;

import java.util.*;
import java.lang.*;
import java.io.*;

class FileSystemUtility {
	public ArrayList<String> generateFileList(String path) {
		ArrayList<String> files = new ArrayList<String>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				if (file.isFile()) {
					files.add(file.getName());
				}
			}
		}
		return files;
	}
}

// dummy
class ReverseMappingUtility {
	private HashMap<String, ArrayList<String>> wordVsConceptHash = new HashMap<>();

	ReverseMappingUtility(ArrayList<String> fileList, String __PATH__) {
		for (String file : fileList) {
			File fileOject = new File(__PATH__ + "/" + file);
			try {
				BufferedReader reader = new BufferedReader(new FileReader(fileOject));
				String line = reader.readLine();

				while (line != null) {
					String first_word = line.split("\\s+")[0];
					String concept = file.split("\\.")[0];

					if (wordVsConceptHash.get(first_word) == null) {
						wordVsConceptHash.put(first_word, new ArrayList<String>());
						wordVsConceptHash.get(first_word).add(concept);
					} else {
						if (!wordVsConceptHash.get(first_word).contains(concept)) {
							wordVsConceptHash.get(first_word).add(concept);
						}
					}
					line = reader.readLine();
				}
				reader.close();
			} catch (Exception e) {
				System.out.println("Error reported" + e);
			}
		}
	}

	public ArrayList<String> findConceptList(String token) {
		ArrayList<String> conceptList = this.wordVsConceptHash.get(token);
		if (conceptList != null) {
			return conceptList;
		} else {
			return null;
		}
	}
}

// dummy
class ConceptTableUtility {
	private HashMap<String, HashMap<String, Boolean>> wordVsConceptTable = new HashMap<>();

	ConceptTableUtility(ArrayList<String> fileList, String __PATH__) {
		for (String file : fileList) {
			HashMap<String, Boolean> inner_map = new HashMap<>();
			String concept = file.split("\\.")[0];
			wordVsConceptTable.put(concept, inner_map);

			File fileOject = new File(__PATH__ + "/" + file);
			try {
				BufferedReader reader = new BufferedReader(new FileReader(fileOject));
				String line = reader.readLine();

				while (line != null) {
					String[] wordList = line.split("\\s+");
					for (String word : wordList) {
						if (!inner_map.containsKey(word)) {
							inner_map.put(word, true);
						}
					}
					line = reader.readLine();
				}
				reader.close();
			} catch (Exception e) {
				System.out.println("Error reported");
			}
		}
	}

	public ArrayList<String> findConceptList(ArrayList<String> conceptList, String token) {
		ArrayList<String> newConceptList = new ArrayList<String>();
		for (String concept : conceptList) {
			HashMap<String, Boolean> tokenVsBooleanMap = this.wordVsConceptTable.get(concept);
			if (tokenVsBooleanMap != null && tokenVsBooleanMap.get(token) != null
					&& tokenVsBooleanMap.get(token) == true) {
				newConceptList.add(concept);
			}
		}

		if (newConceptList.size() != 0) {
			return newConceptList;
		} else {
			return null;
		}
	}
}

public class Concept_parser {
	private String __RESOUCSE_PATH__ = "./resources/";
	private FileSystemUtility fileSystemUtility = new FileSystemUtility();
	private ArrayList<String> fileList;
	private ReverseMappingUtility reverseMappingUtility;
	private ConceptTableUtility conceptTableUtility;

	public Concept_parser() {
		// list all files in concept folder
		this.fileList = fileSystemUtility.generateFileList(__RESOUCSE_PATH__ + "Concept");

		// get utility object for first word tagging
		this.reverseMappingUtility = new ReverseMappingUtility(fileList, __RESOUCSE_PATH__ + "Concept");

		// get utility object for subsequent word tagging
		this.conceptTableUtility = new ConceptTableUtility(fileList, __RESOUCSE_PATH__ + "Concept");
	}

	public String generate_concept(String sentense) {
		sentense = sentense.toLowerCase();
		String[] tokenList = sentense.split("\\s+");
		StringBuilder result = new StringBuilder();

		ArrayList<String> currentTokenConceptList = null;
		for (String token : tokenList) {
			if (currentTokenConceptList == null) {
				currentTokenConceptList = this.reverseMappingUtility.findConceptList(token);
				if (currentTokenConceptList == null) {
					result.append(token + " ");
				}
			} else {
				String prev_concept = currentTokenConceptList.get(0);
				currentTokenConceptList = this.conceptTableUtility.findConceptList(currentTokenConceptList, token);
				if (currentTokenConceptList == null) {
					result.append("{" + prev_concept + "} ");

					currentTokenConceptList = this.reverseMappingUtility.findConceptList(token);
					if (currentTokenConceptList == null) {
						result.append(token + " ");
					}
				}
			}
		}
		return result.toString();
	}
}