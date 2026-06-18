import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.*;

public class DigitalSignTool {
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final String SIGN_ALGORITHM = "SHA256withRSA";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n========================================");
            System.out.println("  ShopSphere Digital Signature Tool");
            System.out.println("========================================");
            System.out.println("1. Generate Key Pair");
            System.out.println("2. Import Existing Key");
            System.out.println("3. Sign Order");
            System.out.println("4. Verify Signature");
            System.out.println("5. Exit");
            System.out.print("Choose: ");
            
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1": generateKeyPair(); break;
                    case "2": importKey(scanner); break;
                    case "3": signOrder(scanner); break;
                    case "4": verifySignature(scanner); break;
                    case "5": System.out.println("Exiting..."); return;
                    default: System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void generateKeyPair() throws Exception {
        System.out.println("Generating RSA-2048 Key Pair...");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(KEY_SIZE);
        KeyPair pair = keyGen.generateKeyPair();

        String pubKeyBase64 = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
        String privKeyBase64 = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());

        Files.write(Paths.get("public.key"), pubKeyBase64.getBytes());
        Files.write(Paths.get("private.key"), privKeyBase64.getBytes());

        String fingerprint = computeFingerprint(pubKeyBase64);

        System.out.println("Success! Keys generated in current directory.");
        System.out.println("  - public.key");
        System.out.println("  - private.key (KEEP THIS SECRET!)");
        System.out.println("Key Fingerprint: " + fingerprint);
    }

    private static void importKey(Scanner scanner) throws Exception {
        System.out.print("Enter path to existing private.key: ");
        String privPath = cleanInput(scanner.nextLine());
        
        byte[] privBytes = Files.readAllBytes(Paths.get(privPath));
        String privStr = new String(privBytes).trim();
        
        // Validate it's a valid RSA private key
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privStr));
        keyFactory.generatePrivate(privSpec); // throws if invalid
        
        Files.write(Paths.get("private.key"), privStr.getBytes());
        System.out.println("Successfully imported private.key to current directory.");
        
        System.out.print("Do you also want to import public.key? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("Enter path to existing public.key: ");
            String pubPath = cleanInput(scanner.nextLine());
            byte[] pubBytes = Files.readAllBytes(Paths.get(pubPath));
            String pubStr = new String(pubBytes).trim();
            
            // Validate public key
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pubStr));
            keyFactory.generatePublic(pubSpec);
            
            Files.write(Paths.get("public.key"), pubStr.getBytes());
            System.out.println("Successfully imported public.key.");
            System.out.println("Key Fingerprint: " + computeFingerprint(pubStr));
        }
    }

    private static void signOrder(Scanner scanner) throws Exception {
        System.out.print("Enter path to order.json (e.g. order_1.json): ");
        String jsonPath = cleanInput(scanner.nextLine());
        
        if (!Files.exists(Paths.get("private.key"))) {
            throw new Exception("private.key not found in current directory! Please generate or import a key first.");
        }
        
        byte[] jsonBytes = Files.readAllBytes(Paths.get(jsonPath));
        String jsonContent = new String(jsonBytes);
        
        // Extract orderId using regex
        Matcher m = Pattern.compile("\"orderId\"\\s*:\\s*(\\d+)").matcher(jsonContent);
        String orderId = "unknown";
        if (m.find()) {
            orderId = m.group(1);
        }
        
        String privKeyStr = new String(Files.readAllBytes(Paths.get("private.key"))).trim();
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privKeyStr));
        PrivateKey privateKey = keyFactory.generatePrivate(privSpec);
        
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(jsonContent.getBytes("UTF-8"));
        byte[] sigBytes = signature.sign();
        
        String sigBase64 = Base64.getEncoder().encodeToString(sigBytes);
        String outFile = "order_" + orderId + ".sig";
        Files.write(Paths.get(outFile), sigBase64.getBytes());
        
        System.out.println("Successfully signed order!");
        System.out.println("Signature saved to: " + outFile);
    }

    private static void verifySignature(Scanner scanner) throws Exception {
        System.out.print("Enter path to order.json: ");
        String jsonPath = cleanInput(scanner.nextLine());
        
        System.out.print("Enter path to order.sig: ");
        String sigPath = cleanInput(scanner.nextLine());
        
        System.out.print("Enter path to public.key: ");
        String pubPath = cleanInput(scanner.nextLine());
        
        byte[] jsonBytes = Files.readAllBytes(Paths.get(jsonPath));
        String jsonContent = new String(jsonBytes);
        
        String sigStr = new String(Files.readAllBytes(Paths.get(sigPath))).trim();
        byte[] sigBytes = Base64.getDecoder().decode(sigStr);
        
        String pubStr = new String(Files.readAllBytes(Paths.get(pubPath))).trim();
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pubStr));
        PublicKey publicKey = keyFactory.generatePublic(pubSpec);
        
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(jsonContent.getBytes("UTF-8"));
        
        boolean isValid = signature.verify(sigBytes);
        if (isValid) {
            System.out.println("\n[+] RESULT: VERIFIED (Signature is VALID)");
        } else {
            System.out.println("\n[-] RESULT: INVALID (Signature verification FAILED)");
        }
    }

    // Loại bỏ BOM (\uFEFF) và ký tự ẩn đầu chuỗi khi user copy-paste path
    private static String cleanInput(String input) {
        if (input == null) return "";
        // Remove BOM and all leading/trailing invisible chars
        return input.trim().replaceAll("^[\\uFEFF\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]+", "");
    }

    private static String computeFingerprint(String base64Key) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(base64Key.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
