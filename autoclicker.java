import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class Emt {
	static JTextField jtfii;
	static boolean quitButtonSet;
	static String stopcode;
	static String keycode;

	static int count;
	static Robot r;
	static int timeBetweenClicks = 0;
	public static void main(String[] args) throws AWTException, NativeHookException {
		quitButtonSet = false;
		count = 0;
		r = new Robot();
		JFrame f = new JFrame();
		JButton jb = new JButton("OK");
		jtfii = new JTextField("Optional", 5);
		JTextField jtf = new JTextField("50", 3);
		JTextField jtfi = new JTextField("F6", 3);
		JLabel jl1 = new JLabel("First box: Click interval (ms)");
		JLabel jl2 = new JLabel("Second box: Start keybind");
		JLabel jl3 = new JLabel("Third box: Quit button (this is optional because start button can work as quit too)");
		f.setSize(500, 200);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setTitle("Autoclickah");
		f.setVisible(true);
		f.getContentPane().setLayout(new FlowLayout());
		f.getContentPane().add(jtf);
		f.getContentPane().add(jtfi);
		f.getContentPane().add(jtfii);
		f.getContentPane().add(jb);
		f.getContentPane().add(jl1);
		f.getContentPane().add(jl2);
		f.getContentPane().add(jl3);

		jb.addActionListener(e -> {
			if(!Objects.equals(jtfi.getText(), "") && !Objects.equals(jtf.getText(), "")) {
				if (parseInt(jtf.getText()) > 1) {
					timeBetweenClicks = parseInt(jtf.getText());
				} else {
					JOptionPane.showMessageDialog(f, "Sorry, 1 ms click interval is not safe. I tried!");
				}
				keycode = jtfi.getText();
			} else {
				JOptionPane.showMessageDialog(f, "Startkey box nor click interval box can be empty");
			}
			if(quitButtonSet) {
				stopcode = jtfii.getText();
			}
		});
		GlobalScreen.registerNativeHook();
		GlobalScreen.addNativeKeyListener(new NativekeyListener());



		jtf.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				jtf.setText("");
			}

			public void focusLost(FocusEvent e) {}
		});
		jtfi.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				jtfi.setText("");
			}

			public void focusLost(FocusEvent e) {}
		});
		jtfii.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				jtfii.setText("");
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(!Objects.equals(jtfii.getText(), "")) {
					quitButtonSet = true;
				}
			}
		});

	}
	public static class MultiThread extends Thread {
		@Override
		public void run() {
			do {
				r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				try {
					Thread.sleep(timeBetweenClicks);
					r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			} while (count % 2 != 0);
		}
	}

	public static class NativekeyListener implements NativeKeyListener {

		@Override
		public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {}

		@Override
		public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
			if(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()).equals(keycode)) {
				count++;
				if(count % 2 != 0) {
					MultiThread th = new MultiThread();
					th.start();
				}
			}
			if(quitButtonSet && NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()).equals(stopcode)) {
				count++;
			}
		}

		@Override
		public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {}
	}
	}