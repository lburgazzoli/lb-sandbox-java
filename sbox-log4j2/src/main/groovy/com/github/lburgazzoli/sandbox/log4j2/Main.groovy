package com.github.lburgazzoli.sandbox.log4j2

import org.apache.logging.log4j.LogManager

class Main {
    public static void main(String[] args) {
        LogManager.getLogger(Main.class).info("test")
    }
}
