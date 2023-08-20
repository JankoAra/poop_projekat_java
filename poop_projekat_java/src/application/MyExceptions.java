package application;

public class MyExceptions {

	public static class FormatChangeUnsuccessful extends Exception {
		private static final long serialVersionUID = -3446748480237480300L;

		public FormatChangeUnsuccessful() {
			super("Promena formata nije uspela.");
		}
	}

	public static class CellUnchanged extends Exception {

		private static final long serialVersionUID = -7206984974593713780L;

		public CellUnchanged() {
			super("Sadrzaj celije nije promenjen.");
		}
	}

	public static class UnsupportedFileFormat extends Exception {

		private static final long serialVersionUID = 5992329876322135856L;

		public UnsupportedFileFormat() {
			super("Format fajla nije podrzan.");
		}
	}

}
