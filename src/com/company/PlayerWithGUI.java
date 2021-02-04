package com.company;

import javax.swing.*;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerWithGUI {
    final private String programName;
    final private LinkedList<Media> songsOrder;
    final private MusicProcessing musicProcessing;

    public PlayerWithGUI(String programName) {
        this.programName = programName;
        songsOrder = new LinkedList<>();
        musicProcessing = new MusicProcessing();

        Thread playing = new Thread(musicProcessing);
        playing.setDaemon(true);
        playing.start();

        System.out.println("Вас приветствует \"" + programName + "\"!");

        runCommandLine();
    }

    //Командная строка
    private void runCommandLine() {
        while (true) {
            String command = JOptionPane.showInputDialog(null,
                    "Введите команду, help - справка:");
            if (command == null) {
                exit();
            }
            command = command.trim().toLowerCase();

            switch (command) {
                case "1":
                case "help":
                    help();
                    break;
                case "2":
                case "exit":
                    exit();
                    return;
                case "3":
                case "add":
                    add();
                    break;
                case "4":
                case "push":
                    push();
                    break;
                case "5":
                case "delete":
                    delete();
                    break;
                case "6":
                case "play":
                    play();
                    break;
                case "7":
                case "start":
                    start();
                    break;
                case "8":
                case "pause":
                    pause();
                    break;
                case "9":
                case "stop":
                    stop();
                    break;
                case "10":
                case "show":
                    show();
                    break;
                default:
                    JOptionPane.showMessageDialog(null,
                            "Команда \"" + command + "\" не распознана, повторите ввод:");
            }
        }
    }

    //Вывод справки
    private void help() {
        JOptionPane.showMessageDialog(null,
                "Введите команду или просто её номер:\n" +
                        "1)  help   - вывести данную справку;\n" +
                        "2)  exit   - выход;\n" +
                        "3)  add    - добавить песню в конец очереди;\n" +
                        "4)  push   - добавить песню после текущей песни;\n" +
                        "5)  delete - удалить песню из очереди;\n" +
                        "6)  play   - прослушать одну песню;\n" +
                        "7)  start  - старт непрерывного прослушивания;\n" +
                        "8)  pause  - поставить на паузу;\n" +
                        "9)  stop   - остановка непрерывного прослушивания;\n" +
                        "10) show   - показать очередь.");
    }

    //Выход
    private void exit() {
        JOptionPane.showMessageDialog(null, programName + " завершена.\n");
        System.exit(0);
    }

    //Ввод названия песни из консоли
    private String inputSongName() {
        String song_name;
        do {
            song_name = JOptionPane.showInputDialog(null,
                    "Введите название песни:");

            if (song_name == null || song_name.trim().length() == 0) {
                JOptionPane.showMessageDialog(null, "Ничего не введено. ");
            } else {
                break;
            }
        } while (true);

        return song_name.trim();
    }

    //Ввод исполнителя песни из консоли
    private String inputSinger() {
        String singer;
        do {
            singer = JOptionPane.showInputDialog(null, "Введите исполнителя песни:");

            if (singer == null || singer.trim().length() == 0) {
                System.out.print("Ничего не введено. ");
            } else {
                break;
            }
        } while (true);

        return singer.trim();
    }

    //Ввод длительности песни из консоли
    private int inputDuration() {
        int duration;
        do {
            try {
                String input = JOptionPane.showInputDialog(null,
                        "Введите длительность песни:");
                duration = Integer.parseInt(input.trim());
                if (duration <= 0) {
                    JOptionPane.showMessageDialog(null,
                            "Длительность песни не может быть ноль или меньше нуля! ");
                } else {
                    break;
                }
            } catch (NumberFormatException | NullPointerException e) {
                JOptionPane.showMessageDialog(null,
                        "Неверный ввод! ");
            }
        } while (true);

        return duration;
    }

    //Добавить песню в конец очереди
    private void add() {
        if (songsOrder.offerLast(new Media(inputSongName(), inputDuration(), inputSinger()))) {
            JOptionPane.showMessageDialog(null,
                    "Песня успешно добавлена в конец очереди.");
        }
    }

    //Добавить песню после текущей песни
    private void push() {
        String message;
        if (songsOrder.size() > 1) {
            message = "Песня успешно добавлена после песни " + songsOrder.get(musicProcessing.currentSongIndex) + ".";
        } else {
            message = "Песня успешно добавлена.";
        }

        if (musicProcessing.currentSongIndex >= songsOrder.size() - 1) {
            songsOrder.add(new Media(inputSongName(), inputDuration(), inputSinger()));
        } else {
            songsOrder.add(musicProcessing.currentSongIndex + 1,
                    new Media(inputSongName(), inputDuration(), inputSinger()));
        }

        JOptionPane.showMessageDialog(null, message);
    }

    //Удалить песню
    private void delete() {
        musicProcessing.stopPlayback(); //удаление во время воспроизведения не поддерживается

        try {
            int removed_index = selectSong();
            JOptionPane.showMessageDialog(null,
                    "Воспроизведение прекращено. " + (removed_index + 1) + " - "
                            + songsOrder.remove(removed_index) + " удалена из очереди.");
        } catch (IllegalStateException ignored) {
        } catch (IndexOutOfBoundsException | NumberFormatException | NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Неверный ввод!");
        }
    }

    //Играть выбранную песню
    private void play() {
        try {
            musicProcessing.startPlayback(selectSong());
        } catch (IllegalStateException ignored) {
        } catch (IndexOutOfBoundsException | NumberFormatException | NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Неверный ввод!");
        }
    }

    //Начать воспроизведение
    private void start() {
        if (songsOrder.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Нет песен!");
        } else {
            musicProcessing.startPlayback();
            System.out.println("Воспроизведение начато!");
        }
    }

    //Поставить на паузу
    private void pause() {
        if (songsOrder.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Нет песен!");
        } else if (musicProcessing.isPlay) {
            musicProcessing.pausePlayback();
            System.out.println("Поставлено на паузу...");
        }
    }

    //Приостановить воспроизведение
    private void stop() {
        if (songsOrder.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Нет песен!");
        } else {
            musicProcessing.stopPlayback();
            System.out.println("Воспроизведение полностью остановлено!");
        }
    }

    //Выбор песни
    private int selectSong() {
        show();
        if (songsOrder.isEmpty()) {
            throw new IllegalStateException();
        }

        String input = JOptionPane.showInputDialog(null, "Введите номер песни:");
        return Integer.parseInt(input.trim()) - 1;
    }

    //Показать очередь
    private void show() {
        if (songsOrder.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Нет песен!");
            return;
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < songsOrder.size(); i++) {
            output.append(i + 1).append(" - ").append(songsOrder.get(i)).append(System.lineSeparator());
        }
        JOptionPane.showMessageDialog(null, output);
    }

    class MusicProcessing implements Runnable {
        private int currentSongIndex;
        private int currentPosition; //позиция воспроизведения песни с шагом 100 мс
        private boolean isPlay;
        private boolean onlyOneSong; //режим воспроизведения - выбранная песня или вся очередь

        @Override
        public void run() {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(
                    () -> playback(),
                    0,
                    100,
                    TimeUnit.MILLISECONDS);
        }

        private void playback() {
            if (!isPlay) {
                return;
            }

            if (currentPosition <= songsOrder.get(currentSongIndex).duration * 10) {//достигнут ли конец песни

                if (currentPosition == 0) {
                    System.out.print(songsOrder.get(currentSongIndex) + " начала играть");
                }
                currentPosition++;
            } else {
                System.out.println(" / закончила играть.");
                currentPosition = 0;
                //сменить песню
                if (currentSongIndex < songsOrder.size() - 1 && !onlyOneSong) {  //есть ли ещё песня
                    currentSongIndex++;
                } else {
                    stopPlayback();
                    onlyOneSong = false;
                    System.out.println("Воспроизведение завершено!");
                }
            }
        }

        private void startPlayback() {
            isPlay = true;
        }

        private void startPlayback(int selectedSongIndex) {
            startPlayback();
            onlyOneSong = true;
            currentSongIndex = selectedSongIndex;
            currentPosition = 0;
        }

        private void pausePlayback() {
            isPlay = false;
        }

        private void stopPlayback() {
            isPlay = false;
            currentSongIndex = 0;
            currentPosition = 0;
        }
    }
}

