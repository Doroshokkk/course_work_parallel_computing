import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class BuildIndex extends Thread{

     List<File> filesList;
     HashMap<String, List<String>> invertedIndex = new HashMap<>();
     boolean done = false;
    public BuildIndex(List<File> filesList){
        this.filesList = filesList;
    }

    public void run(){
        Scanner in = new Scanner(System.in);
        for (File file : filesList) {
            StringBuilder fileContent = new StringBuilder();

            try {
                in = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (in.hasNext()) {
                fileContent.append(in.nextLine());
            }

            Set<String> contentWords = new HashSet<>(Arrays.asList(lineRedactor(fileContent).split(" ")));
            for (String word : contentWords) {

                if (invertedIndex.containsKey(word)) {
                    invertedIndex.get(word).add(file.getName());
                } else {
                    List<String> positionsList = new LinkedList<>();
                    positionsList.add(file.getName());
                    invertedIndex.put(word, positionsList);
                }
            }
        }
        done = true;
    }
    public static String lineRedactor(StringBuilder rawLine) {

        return rawLine.toString().toLowerCase().replaceAll("\\s+", " ").replaceAll("<.*?>", "").replaceAll("[^a-z\\s]", "");

    }
    public  HashMap<String, List<String>> sendData(){
        return invertedIndex;
    }



}
