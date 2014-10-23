package org.pack;

import javax.swing.*;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.event.*;

class Simple {
	public static void main(String[] args) throws Exception{
		System.out.println(Simple.class.getName());

		ClassLoader cl = ClassLoader.getSystemClassLoader();
		System.out.println(cl.loadClass("org.pack.Simple").getName());
	}
}

class Another {
	class Inner {}
}
