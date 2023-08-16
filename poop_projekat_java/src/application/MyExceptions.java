package application;

public class MyExceptions {

	public static class FormatChangeUnsuccessful extends Exception {
		private static final long serialVersionUID = -3446748480237480300L;

		public FormatChangeUnsuccessful() {
			super("Promena formata nije uspela.");
		}
	}

	public static class CellUnchanged extends Exception {

		private static final long serialVersionUID = 1L;

		public CellUnchanged() {
			super("Sadrzaj celije nije promenjen.");
		}
	}

}
