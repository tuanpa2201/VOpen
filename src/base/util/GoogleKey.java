package base.util;

public class GoogleKey {
	private static final String[] keys = { "AIzaSyAocRnSkbbpbc2yed6l-JnO28vmLo5Z0n0",
			"AIzaSyBO_GJDyViq9-TO3Wbwitgh4JfboqCmOTs", "AIzaSyDcZpeI-H0LEkJSQGb2yTAnHw9cLt-_eqA",
			"AIzaSyDpgva8OkTfIRc2ti8qY4fiKrN5vcE2PF0", "AIzaSyAwdtISopf1irIJi-ZE5o8SzpKkNTHyl2M",
			"AIzaSyATEJK-a1j_E0Bi6aGsjbmef14HUJP2RUo", "AIzaSyCui8lxVKVku1AmcriBPiSJ9O8BSI_d_HM",
			"AIzaSyAl8uYmjwIooSXJ8dbWMhqH4zHFmkH-3Co", "AIzaSyB4V4FdbVGjrHy_enECQjv9RGD9kV24STQ",
			"AIzaSyD81cRJ9IYtSE_vfDeyOnTRFtdKqFM0NW8", "AIzaSyCmD6lWSSh0eJW8tYC2-2h2BXcCMNSE8nE",
			"AIzaSyCxyLRr3_XRk1Ho_xk88ITtr89XNsitFt", "AIzaSyA4ek8ZoMTgPjvl4F6SDvc4KMKUYxoJZ8",
			"AIzaSyBiwA7FpGHQ0CAOSw5ecpF0XBk8XD2KHEI", "AIzaSyBv-s3e4e_ggracDyp3dnvsGH4dlzLzhQM",
			"AIzaSyDxMwa0C7-Z2vjUJl_8dC2TV_5mqtwn0Uc", "AIzaSyCMRJpLXnfeSaJBTfSXP55s-rNdwGXGiC8",
			"AIzaSyCz8D88oUfcpe0rBpf8SQpNu8BWlhXdlt0", "AIzaSyDFYk4dTXBxwbekBggXL2P6Rm3Iz4TXrio",
			"AIzaSyAcT_pPoziGulkjTto_nYxnPSORUkDq3h0", "AIzaSyBS2lBgpkgoOAbP3P-RKJRP0yzLKEiwOhE" };
	private static int count = 0;

	public static String getKey() {
		String key = keys[count];
		count++;
		if (count >= keys.length) {
			count = 0;
		}
		return key;
	}

	public static int getSizeKey() {
		return keys.length;
	}

	public static String getKey(int keyIndex) {
		String key = "";
		if (0 <= keyIndex && keyIndex < keys.length) {
			key = keys[keyIndex];
		}
		return key;
	}
}
