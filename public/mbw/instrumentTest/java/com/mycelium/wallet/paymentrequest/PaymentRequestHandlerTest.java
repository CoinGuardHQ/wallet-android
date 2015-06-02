/*
 * Copyright 2013, 2014 Megion Research and Development GmbH
 *
 * Licensed under the Microsoft Reference Source License (MS-RSL)
 *
 * This license governs use of the accompanying software. If you use the software, you accept this license.
 * If you do not accept the license, do not use the software.
 *
 * 1. Definitions
 * The terms "reproduce," "reproduction," and "distribution" have the same meaning here as under U.S. copyright law.
 * "You" means the licensee of the software.
 * "Your company" means the company you worked for when you downloaded the software.
 * "Reference use" means use of the software within your company as a reference, in read only form, for the sole purposes
 * of debugging your products, maintaining your products, or enhancing the interoperability of your products with the
 * software, and specifically excludes the right to distribute the software outside of your company.
 * "Licensed patents" means any Licensor patent claims which read directly on the software as distributed by the Licensor
 * under this license.
 *
 * 2. Grant of Rights
 * (A) Copyright Grant- Subject to the terms of this license, the Licensor grants you a non-transferable, non-exclusive,
 * worldwide, royalty-free copyright license to reproduce the software for reference use.
 * (B) Patent Grant- Subject to the terms of this license, the Licensor grants you a non-transferable, non-exclusive,
 * worldwide, royalty-free patent license under licensed patents for reference use.
 *
 * 3. Limitations
 * (A) No Trademark License- This license does not grant you any rights to use the Licensor’s name, logo, or trademarks.
 * (B) If you begin patent litigation against the Licensor over patents that you think may apply to the software
 * (including a cross-claim or counterclaim in a lawsuit), your license to the software ends automatically.
 * (C) The software is licensed "as-is." You bear the risk of using it. The Licensor gives no express warranties,
 * guarantees or conditions. You may have additional consumer rights under your local laws which this license cannot
 * change. To the extent permitted under your local laws, the Licensor excludes the implied warranties of merchantability,
 * fitness for a particular purpose and non-infringement.
 */

package com.mycelium.wallet.paymentrequest;

import com.google.common.base.Optional;
import com.mrd.bitlib.model.NetworkParameters;
import com.mycelium.wallet.BitcoinUri;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PaymentRequestHandlerTest extends TestCase {

   private PaymentRequestInformation pi;
   private CountDownLatch latch = new CountDownLatch(1);

   public void testFromBitcoinUri() throws Exception {

      //todo: Make tests without call to external service
      Optional<BitcoinUri> bitcoinUri = BitcoinUri.parse("bitcoin:mfdZ7PKAxp7eckWYykC6xzKu4R3dDKJ3qG?r=http%3A%2F%2Fbip70.mycelium.com%2Ff.php%3Fh%3D99740db7e60c3f079bb8bca22f7f1c4c&amount=0.001", NetworkParameters.testNetwork);


      // not sig
      // Optional<BitcoinUri> bitcoinUri = BitcoinUri.parse("bitcoin:125HoD6zNb8tnsqWxeWQcex9mgEsmy2Fpd?r=http://dblsha.com/payreq/ZTJhMD&amount=0.01", NetworkParameters.productionNetwork);
      Bus bus = new Bus();
      PaymentRequestHandler paymentRequestHandler = new PaymentRequestHandler(bus, NetworkParameters.testNetwork);

      paymentRequestHandler.fromBitcoinUri(bitcoinUri.get());

      // wait for callback via otto
      latch.await(5, TimeUnit.SECONDS);


      PkiVerificationData pkiVerificationData = pi.getPkiVerificationData();

      assertEquals("check signature", "BitPay, Inc., Atlanta, US", pkiVerificationData.displayName);

   }

   @Subscribe
   public void onPaymentRequestFetched(PaymentRequestInformation paymentRequestInformation){
      pi = paymentRequestInformation;
      latch.countDown();
   }

}