import java.io.*;
import java.util.Scanner;
import java.util.*;
import java.util.stream.Collectors;


public class main {
    public static HashMap<String, List<String>> invertedIndexPart = new HashMap<>();
    public static HashMap<String, List<String>> fullInvertedIndex = new HashMap<>();
    public static Scanner in = new Scanner(System.in);
    public static File sourceDirectory;
    public static int threadsAmount;
    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("Choose the directory: ");
        String dir = in.nextLine();
        sourceDirectory = new File(dir);

        List<File> filesList = Arrays.asList(Objects.requireNonNull(sourceDirectory.listFiles()));

        System.out.println("How many threads u want?");
        threadsAmount = in.nextInt();

        long startTime = System.currentTimeMillis();

        BuildIndex[] ThreadArray = new BuildIndex[threadsAmount];
        for (int i=0; i<threadsAmount; i++){
            List<File> subFilesList = new ArrayList<>();
            for (int j = i; j<filesList.size(); j+=threadsAmount){
                subFilesList.add(filesList.get(j));
            }
            ThreadArray[i] = new BuildIndex(subFilesList);
            ThreadArray[i].start();
        }

        System.out.println("All threads are set to work now!");

        int doneThreads = 0;

        do{
            for (int i = 0; i<threadsAmount; i++){
                if(ThreadArray[i].done){
                    ThreadArray[i].done = false;
                    doneThreads++;
                    System.out.println("Thread number "+ i + " finished his job!");
                    invertedIndexPart = ThreadArray[i].sendData();
                    for (HashMap.Entry<String, List<String>> pair : invertedIndexPart.entrySet()) {
                        for (String pos : pair.getValue()) {
                            if (fullInvertedIndex.containsKey(pair.getKey())) {
                                fullInvertedIndex.get(pair.getKey()).add(pos);
                            } else {
                                List<String> positionsList = new LinkedList<>();
                                positionsList.add(pos);
                                fullInvertedIndex.put(pair.getKey(), positionsList);
                            }
                        }
                    }
                }
            }
        }while(doneThreads<threadsAmount);

        long endTime = System.currentTimeMillis();

        long time = endTime - startTime;
        System.out.println(time);

        for (int i = 0; i<threadsAmount; i++){
            ThreadArray[i].join();
        }

        OutputToFile(dir, time);


    }

    public static void OutputToFile(String dir, long time) throws IOException {

        List<String> wordList = new ArrayList<>(fullInvertedIndex.keySet()).stream().sorted().collect(Collectors.toList());

        String fileName;
        fileName = dir+"_"+threadsAmount+ "_threads_"+time+"millis"+".txt";
        BufferedWriter our = new BufferedWriter(new FileWriter(fileName));

        for (String word : wordList) {
            StringBuilder listIndex = new StringBuilder(word + ": ");

            for (String pos : fullInvertedIndex.get(word)) {
                listIndex.append(pos).append("  ");
            }

            listIndex.append("\n\n\n");
            our.write(listIndex.toString());
            our.flush();
        }
        System.out.println(fileName);
    }
}