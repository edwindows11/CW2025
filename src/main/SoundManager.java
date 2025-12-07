package com.comp2042;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class SoundManager {

    private static SoundManager instance;
    private Clip backgroundMusic;
    private boolean isMuted = false;
    private List<String> playlist;
    private int currentPlaylistIndex = 0;
    private Clip gameOverClip;

    private SoundManager() {
        // Private constructor for singleton
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playBackgroundMusic(String fileName) {
        playlist = null; // Disable playlist mode
        startMusic(fileName, true);
    }

    public void playPlaylist(List<String> files) {
        if (files == null || files.isEmpty())
            return;
        playlist = files;
        currentPlaylistIndex = 0;
        startMusic(playlist.get(currentPlaylistIndex), false);
    }

    private void startMusic(String fileName, boolean loop) {
        if (isMuted)
            return;

        try {
            stopCurrentMusic();

            URL resource = getClass().getClassLoader().getResource(fileName);
            if (resource != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resource);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioInputStream);

                if (loop) {
                    backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                } else {
                    // Add listener to play next song when this one finishes
                    backgroundMusic.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            // Check if we are still in playlist mode and it wasn't manually stopped
                            if (playlist != null && backgroundMusic != null && !isMuted) {
                                backgroundMusic.close(); // Close resources
                                currentPlaylistIndex = (currentPlaylistIndex + 1) % playlist.size();
                                startMusic(playlist.get(currentPlaylistIndex), false);
                            }
                        }
                    });
                }

                backgroundMusic.start();
            } else {
                System.out.println("Music file not found: " + fileName);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    private void stopCurrentMusic() {
        if (backgroundMusic != null) {
            // Remove listeners to prevent auto-triggering next song on manual stop
            // (Though standard removeLineListener requires reference to the specific
            // listener object,
            // closing the clip will trigger STOP, so handle null playlist check in
            // listener)
            if (backgroundMusic.isRunning()) {
                backgroundMusic.stop();
            }
            backgroundMusic.close();
            backgroundMusic = null;
        }
    }

    public void stopBackgroundMusic() {
        playlist = null; // Clear playlist to stop rotation
        stopCurrentMusic();
    }

    public void stopGameOverSound() {
        if (gameOverClip != null) {
            if (gameOverClip.isRunning()) {
                gameOverClip.stop();
            }
            gameOverClip.close();
            gameOverClip = null;
        }
    }

    public void playSound(String fileName) {
        if (isMuted)
            return;

        try {
            URL resource = getClass().getClassLoader().getResource(fileName);
            if (resource != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resource);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);

                if ("gameover.wav".equals(fileName)) {
                    stopGameOverSound(); // Stop previous if any
                    gameOverClip = clip;
                }

                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    public void toggleSound() {
        isMuted = !isMuted;
        if (isMuted) {
            if (backgroundMusic != null && backgroundMusic.isRunning()) {
                backgroundMusic.stop(); // This will trigger STOP event
            }
        } else {
            // Resume current track
            if (backgroundMusic != null) {
                backgroundMusic.start();
                if (playlist == null) {
                    backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                }
            } else {
                // Restart if it was fully stopped or null
                if (playlist != null) {
                    startMusic(playlist.get(currentPlaylistIndex), false);
                }
                // Note: Resume logic for single track is simplified here, assumes it was
                // running.
            }
        }
    }

    public boolean isMuted() {
        return isMuted;
    }
}
