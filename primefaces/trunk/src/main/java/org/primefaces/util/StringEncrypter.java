package org.primefaces.util;

// CIPHER / GENERATORS
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

// KEY SPECIFICATIONS
import java.security.spec.KeySpec;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEParameterSpec;

// EXCEPTIONS
import javax.faces.FacesException;

/**
 * ----------------------------------------------------------------------------- The following example implements a class for encrypting and decrypting strings
 * using several Cipher algorithms. The class is created with a key and can be used repeatedly to encrypt and decrypt strings using that key. Some of the more
 * popular algorithms are: Blowfish DES DESede PBEWithMD5AndDES PBEWithMD5AndTripleDES TripleDES
 *
 * @version 1.0
 * @author Jeffrey M. Hunter (jhunter@idevelopment.info)
 * @author http://www.idevelopment.info -----------------------------------------------------------------------------
 */
public class StringEncrypter {

	private static final Logger LOG = Logger.getLogger(StringEncrypter.class.getName());
	
    private Cipher ecipher;
    private Cipher dcipher;

    /**
     * Constructor used to create this object. Responsible for setting and initializing this object's encrypter and decrypter Chipher instances given a Secret
     * Key and algorithm.
     *
     * @param key Secret Key used to initialize both the encrypter and decrypter instances.
     * @param algorithm Which algorithm to use for creating the encrypter and decrypter instances.
     */
    public StringEncrypter(SecretKey key, String algorithm) {
        try {
            ecipher = Cipher.getInstance(algorithm);
            dcipher = Cipher.getInstance(algorithm);
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            throw new FacesException("Could not initialize Cipher objects", e);
        }
    }

    /**
     * Constructor used to create this object. Responsible for setting and initializing this object's encrypter and decrypter Chipher instances given a Pass
     * Phrase and algorithm.
     *
     * @param passPhrase Pass Phrase used to initialize both the encrypter and decrypter instances.
     */
    public StringEncrypter(String passPhrase) {

        // 8-bytes Salt
        byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x34, (byte) 0xE3, (byte) 0x03
        };

        // Iteration count
        int iterationCount = 19;

        try {

            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

            ecipher = Cipher.getInstance("PBEWithMD5AndDES");
            dcipher = Cipher.getInstance("PBEWithMD5AndDES");

            // Prepare the parameters to the cipthers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

        } catch (Exception e) {
            throw new FacesException("Could not initialize Cipher objects", e);
        }
    }

    /**
     * Takes a single String as an argument and returns an Encrypted version of that String.
     *
     * @param str String to be encrypted
     * @return <code>String</code> Encrypted version of the provided String
     */
    public String encrypt(String str) {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            return Base64.encodeToString(enc, false);

        } catch (Exception e) {
        	LOG.log(Level.WARNING, "Could not encrypt string", e);
        }

        return null;
    }

    /**
     * Takes a encrypted String as an argument, decrypts and returns the decrypted String.
     *
     * @param str Encrypted String to be decrypted
     * @return <code>String</code> Decrypted version of the provided String
     */
    public String decrypt(String str) {

        try {

            // Decode base64 to get bytes
            byte[] dec = Base64.decode(str);

            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");

        } catch (Exception e) {
        	LOG.log(Level.WARNING, "Could not decrypt string", e);
        }

        return null;
    }
}