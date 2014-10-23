import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

abstract class MyActionListener extends java.awt.Point implements ActionListener {
	public abstract void actionPerformed(ActionEvent e);
	public int inherField = 15;
}

interface IZoomable {
	public void zoom();
}

class InheritedParams08 {
	void foo() {
		JButton jb = new JButton("button");
		jb.addActionListener(new ActionListener(){

			private int field;

			public void actionPerformed(ActionEvent e) {
				// omp parallel
				{
					ActionEvent tmpE = e;
					int tmpField = field;
					int tmpField2 = this.field;
				}
			}
		});

		jb.addActionListener(new MyActionListener(){

			private int field;

			public void actionPerformed(ActionEvent e) {
				// omp parallel
				{
					ActionEvent tmpE = e;
					int tmpField = field;
					int tmpField2 = this.field;
					int tmpIF = inherField;
					int tmpIF2 = this.inherField;
				}
			}
		});

		jb.addActionListener(new MyActionListener() {

			private int field;

			public void actionPerformed(ActionEvent e) {
				// omp parallel
				{
					ActionEvent tmpE = e;
					int tmpField = field;
					int tmpField2 = this.field;
					int tmpIF = inherField;
					int tmpIF2 = this.inherField;
				}
			}

			public void zoom() {

			}
		});

	}
}
