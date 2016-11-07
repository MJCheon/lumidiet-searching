package com.cse;

import com.cse.module.DocSearchModule;
import com.cse.network.Word2VecServer;

import java.util.Scanner;

/**
 * Created by bullet on 16. 11. 2.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Word2VecServer word2VecServer = new Word2VecServer();
        word2VecServer.startServer();
//        DocSearchModule docSearchModule = new DocSearchModule();
//        Scanner scanner = new Scanner(System.in);
//        String word = scanner.nextLine();
//        docSearchModule.search(word);
    }
}
