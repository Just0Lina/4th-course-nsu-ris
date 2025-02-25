package com.crackhash.worker_service.util;

import java.util.ArrayList;
import java.util.Arrays;

import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.paukov.combinatorics.CombinatoricsFactory.createPermutationWithRepetitionGenerator;
import static org.paukov.combinatorics.CombinatoricsFactory.createVector;

public class AlphabetGenerator {
    private static final Logger logger = LoggerFactory.getLogger(AlphabetGenerator.class);

    private static final List<String> ALPHABET = Arrays.asList(
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    );
    private final int maxLength;

    public AlphabetGenerator(int maxLength) {
        this.maxLength = maxLength;
    }

    public List<String> getPart(int partNumber, int partCount) {
        logger.info("Generating part {} of {} for words with maxLength: {}", partNumber + 1, partCount, maxLength);

        ICombinatoricsVector<String> vector = createVector(ALPHABET);
        List<String> partWords = new ArrayList<>();

        for (int wordSize = maxLength; wordSize > 0; wordSize--) {
            Generator<String> generator = createPermutationWithRepetitionGenerator(vector, wordSize);

            int totalSize = (int) generator.getNumberOfGeneratedObjects();
            int chunkSize = totalSize / partCount;
            int startIndex = partNumber * chunkSize;
            int endIndex = (partNumber == partCount - 1) ? totalSize : (partNumber + 1) * chunkSize;
            int index = 0;

            for (ICombinatoricsVector<String> word : generator) {
                if (index >= startIndex && index < endIndex) {
                    partWords.add(convertToString(word));
                }
                index++;
                if (index >= endIndex) break;
            }
        }
        logger.info("Generated word list");
        return partWords;
    }


    public String convertToString(ICombinatoricsVector<String> vector) {
        StringBuilder sb = new StringBuilder();
        for (String element : vector) {
            sb.append(element);
        }

        return sb.toString();
    }
}
