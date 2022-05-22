import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CpsTest {
    static String keycode;
    static JButton startbutton ;
    static JButton pausebutton;
    static JButton quitbutton;
    static JButton popupbutton;
    static boolean keybindsChanged;
    static ArrayList<String> keybinds;
    static HashMap<JButton, String> defaultKeys;
    static HashMap<JButton, Integer> holder;
    static HashMap<String, JButton> keybindsRev;
    static JFrame popupFrame;
    static JFrame f;
    static JLabel l;
    static double count;
    static boolean stop;
    public static void main(String[] args) throws NativeHookException {
        keycode = "P";
        defaultKeys = new HashMap<>();
        holder = new HashMap<>();
        keybindsChanged = false;
        keybinds = new ArrayList<>();
        keybindsRev = new HashMap<>();

        JPanel popPanel = new JPanel();
        popPanel.setLayout(new FlowLayout());
        JLabel psinfo = new JLabel("Pause keybind");
        JTextField startjtf = new JTextField(3);
        JTextField pausejtf = new JTextField(3);
        JTextField quitjtf = new JTextField(3);
        JLabel quitinfo = new JLabel("Quit keybind");
        JLabel strtinfo = new JLabel("Start Keybind");
        JButton okbutton = new JButton("OK!");
        popupFrame = new JFrame();
        popupFrame.setSize(300, 150);
        popupFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        popupFrame.setTitle("Keybind Management");
        popupFrame.setVisible(false);
        popupFrame.add(popPanel);
        popPanel.add(strtinfo);
        popPanel.add(startjtf);
        popPanel.add(psinfo);
        popPanel.add(pausejtf);
        popPanel.add(quitinfo);
        popPanel.add(quitjtf);
        popPanel.add(okbutton);

        stop = false;
        count = 0.0;
        startbutton = new JButton();
        pausebutton = new JButton();
        quitbutton = new JButton();
        popupbutton = new JButton();

        defaultKeys.put(startbutton, "S");
        defaultKeys.put(pausebutton, "P");
        defaultKeys.put(quitbutton, "Q");

        holder.put(startbutton, 1);
        holder.put(pausebutton, 2);
        holder.put(quitbutton, 3);

        okbutton.addActionListener(e -> {
            if(!Objects.equals(startjtf.getText(), "") && !Objects.equals(pausejtf.getText(), "") && !Objects.equals(quitjtf.getText(), "")) {
                keycode = pausejtf.getText();
                startbutton.setText("Start (" + startjtf.getText() + ")");
                pausebutton.setText("Pause (" + pausejtf.getText() + ")");
                quitbutton.setText("Quit (" + quitjtf.getText() + ")");
                keybinds.add(startjtf.getText());
                keybinds.add(quitjtf.getText());
                keybinds.add(pausejtf.getText());
                keybindsRev.put(startjtf.getText(), startbutton);
                keybindsRev.put(quitjtf.getText(), quitbutton);
                keybindsRev.put(pausejtf.getText(), pausebutton);
                keybindsChanged = true;
            } else {
                JOptionPane.showMessageDialog(popupFrame, "Empty textfield(s)");
            }
        });
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(new Nativehook());
        f = new JFrame();
        l = new JLabel("0.0", SwingConstants.CENTER);
        f.setTitle("Stopwatch");
        f.setSize(500, 500);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
        f.setLayout(new BorderLayout());
        l.setFont(new Font("Serif", Font.PLAIN, 50));
        pausebutton.setText("Pause (P)");
        quitbutton.setText("Quit (Q)");
        popupbutton.setText("Manage keybindings");
        startbutton.setText("Start (S)");
        f.getContentPane().add(l, BorderLayout.CENTER);
        f.getContentPane().add(startbutton, BorderLayout.PAGE_START);
        f.getContentPane().add(pausebutton, BorderLayout.LINE_START);
        f.getContentPane().add(quitbutton, BorderLayout.LINE_END);
        f.getContentPane().add(popupbutton, BorderLayout.PAGE_END);

        startbutton.addActionListener(e -> {
            pausebutton.setText("Pause (" + keycode + ")");
            stop = false;
            count = 0.0;
            l.setText(String.valueOf(count));
            Thhread th = new Thhread();
            th.start();
        });
        quitbutton.addActionListener(e -> {
            stop = true;
            l.setText("0.0");
            count = 0.0;
        });
        pausebutton.addActionListener(e -> {
            if(pausebutton.getText().contains("Pause")) {
                stop = true;
                pausebutton.setText("Continue (" + keycode + ")");
            } else if(pausebutton.getText().contains("Continue")) {
                stop = false;
                Thhread th = new Thhread();
                th.start();
                pausebutton.setText("Pause (" + keycode + ")");
            }
        });
        popupbutton.addActionListener(e -> popupFrame.setVisible(true));
    }

    public static class Thhread extends Thread {
        @Override
        public void run() {
            do {
                count += 0.1;
                l.setText(String.valueOf(count));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (!stop);
        }
    }
    public static class Nativehook implements NativeKeyListener {

        @Override
        public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

        }

        @Override
        public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
            if(keybindsChanged) {
                if(contains(keybinds, NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()))) {
                    String keycode = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
                    performTask(holder.get(keybindsRev.get(keycode)));
                }
            } else {
                String keycode = NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
                if(defaultKeys.get(startbutton).equals(keycode)) {
                    performTask(1);
                } else if(defaultKeys.get(pausebutton).equals(keycode)) {
                    performTask(2);
                } else if(defaultKeys.get(quitbutton).equals(keycode)) {
                    performTask(3);
                }
            }
        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

        }
    }
    public static boolean contains(ArrayList<String> al, String s) {
        for (String value : al) {
            if (value.equals(s)) {
                return true;
            }
        }
        return false;
    }
    public static void performTask(int o) {
        Thhread th = new Thhread();
        switch(o) {
            case 1:
                pausebutton.setText("Pause (" + keycode + ")");
                stop = false;
                count = 0.0;
                l.setText(String.valueOf(count));
                th.start();
                break;
            case 2:
                if(pausebutton.getText().contains("Pause")) {
                    stop = true;
                    pausebutton.setText("Continue (" + keycode + ")");
                } else if(pausebutton.getText().contains("Continue")) {
                    stop = false;
                    th = new Thhread();
                    th.start();
                    pausebutton.setText("Pause (" + keycode + ")");
                }
                break;
            case 3:
                stop = true;
                l.setText("0.0");
                count = 0.0;
                break;
        }
    }
}