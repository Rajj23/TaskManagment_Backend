package com.taskManagment.demo.Custom;

import com.taskManagment.demo.Entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskTrie {

    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        List<Task> tasks = new ArrayList<>();
        boolean isEndOfWord = false;
    }

    private TrieNode root = new TrieNode();

    // Insert task title and description into trie
    public void insert(String word, Task task){
        if(word == null || word.trim().isEmpty()) return;

        TrieNode node = root;
        for(char c: word.toLowerCase().toCharArray()){
            // Skip spaces and special characters for better matching
            if(Character.isLetterOrDigit(c)) {
                node = node.children.computeIfAbsent(c, k->new TrieNode());
                node.tasks.add(task);
            }
        }
        node.isEndOfWord = true;
    }

    // Insert both title and description for comprehensive search
    public void insertTask(Task task) {
        if(task.getTitle() != null) {
            insert(task.getTitle(), task);
            // Also insert individual words from title
            String[] titleWords = task.getTitle().toLowerCase().split("\\s+");
            for(String word : titleWords) {
                if(word.length() > 0) {
                    insert(word, task);
                }
            }
        }

        if(task.getDescription() != null) {
            insert(task.getDescription(), task);
            // Also insert individual words from description
            String[] descWords = task.getDescription().toLowerCase().split("\\s+");
            for(String word : descWords) {
                if(word.length() > 0) {
                    insert(word, task);
                }
            }
        }
    }

    public List<Task> searchByPrefix(String prefix){
        if(prefix == null || prefix.trim().isEmpty()) return new ArrayList<>();

        TrieNode node = root;
        String cleanPrefix = prefix.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");

        for(char c : cleanPrefix.toCharArray()){
            node = node.children.get(c);
            if(node == null) return new ArrayList<>();
        }

        // Remove duplicates and return unique tasks
        return node.tasks.stream().distinct().collect(Collectors.toList());
    }

    // Clear all data (useful when rebuilding)
    public void clear() {
        root = new TrieNode();
    }

    // Check if trie is empty
    public boolean isEmpty() {
        return root.children.isEmpty();
    }
}
