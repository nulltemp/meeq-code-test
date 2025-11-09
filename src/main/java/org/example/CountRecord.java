package org.example;

import java.util.Map;

public record CountRecord(long time, Map<Integer, Integer> counts) {
}
