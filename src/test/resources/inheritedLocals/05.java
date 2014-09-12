import java.util.ArrayList;
import javax.swing.JButton;
import java.awt.event.*;

class InheritedLocal05 {

	public int wrongField1;

	public static void foo(int w) {
		int wrong0;
	}

	public int wrongField2;

	public static void main(String[] args) {

		int ok1;

		JButton button = new JButton();
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int ok2;

				// omp parallel
				{
					int wrong = 4;
					foo(wrong);
				}
			}
		});

		for (int i = 0; i < 10; i++) {
			int wrong2;
			if (i < 5) { String wrong3 = "foo"; }
			int wrong4 = 3;
		}

		double wrong5 = 1.2;
	}

	public int wrongField4;
}
