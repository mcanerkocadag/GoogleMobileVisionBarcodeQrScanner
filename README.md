GOOGLE MOBILE VISON BARCODE-QR SCANNER 
KOTLİN SUPPORT

Step 1: Add it in your root build.gradle at the end of repositories:

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2: Add the dependency
implementation 'com.github.mcanerkocadag:GoogleMobileVisionBarcodeQrScanner:0.1.1'

Step 3: Open Camera and Read Barcode
startActivityForResult(
                Intent(
                    activity,
                    ScannedBarcodeActivity::class.java
                ), ScannedBarcodeActivity.SCAN_REQUEST_CODE
            )
            
Step 4: Result
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ScannedBarcodeActivity.SCAN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getStringExtra("result")
                Toast.makeText(activity,""+result, Toast.LENGTH_LONG).show()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        super.onActivityResult(requestCode,resultCode,data)
    }
    
    You can examine the sample project.
