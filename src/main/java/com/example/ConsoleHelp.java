package com.example;

public class ConsoleHelp {

    // Statisk metod eftersom den inte behöver någon instans och kan anropas direkt via klassen
    public static void showHelp() {
        System.out.println();
        System.out.println("╔════════════════════════╗");
        System.out.println("║        U S A G E       ║");
        System.out.println("╚════════════════════════╝");
        System.out.println("""
                --zone SE1|SE2|SE3|SE4    (required) Specify electricity zone
                --date YYYY-MM-DD         (optional) Defaults to today
                --sorted                  (optional) Display prices in descending order
                --charging 2h|4h|8h       (optional) Find optimal charging windows
                --help                    (optional) Show this help message
              ╠══════════════════════════════════════════════════════════════════════════════════╣
              ║                             EXAMPLE USAGE                                        ║
              ║                                                                                  ║
              ║ java -cp target/classes com.example.Main --zone SE3 --date 2025-09-04            ║
              ║ java -cp target/classes com.example.Main --zone SE1 --charging 4h                ║
              ║ java -cp target/classes com.example.Main --zone SE2 --date 2025-09-04 --sorted   ║
              ║ java -cp target/classes com.example.Main --help                                  ║
              ╠══════════════════════════════════════════════════════════════════════════════════╣
              """);
        System.out.println();
    }
}