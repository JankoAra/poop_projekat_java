package application;

public class MyExceptions {

	public static class FormatChangeUnsuccessful extends Exception {
		private static final long serialVersionUID = -3446748480237480300L;

		public FormatChangeUnsuccessful() {
			super("Promena formata nije uspela.");
		}
	}

}
