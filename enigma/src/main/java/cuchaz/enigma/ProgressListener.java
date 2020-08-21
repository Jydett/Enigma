package cuchaz.enigma;

public interface ProgressListener {
	static ProgressListener none() {
		return new ProgressListener() {
			@Override
			public void init(int totalWork, String title) {
			}

			@Override
			public void step(int numDone, String message) {
			}
		};
	}

	void init(int totalWork, String title);

	void step(int numDone, String message);
}
