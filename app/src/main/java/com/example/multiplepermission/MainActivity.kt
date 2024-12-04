package com.example.multiplepermission

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.multiplepermission.ui.theme.MultiplePermissionTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val notificationGranted = permissions[android.Manifest.permission.POST_NOTIFICATIONS] ?: false
        val cameraGranted = permissions[android.Manifest.permission.CAMERA] ?: false

        if (notificationGranted) {
            Toast.makeText(applicationContext, "알림 권한 허용됨", Toast.LENGTH_SHORT).show()
        } else {
            handlePermissionDenied(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if (cameraGranted) {
            Toast.makeText(applicationContext, "카메라 권한 허용됨", Toast.LENGTH_SHORT).show()
        } else {
            handlePermissionDenied(android.Manifest.permission.CAMERA)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MultiplePermissionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ){
                        Button(
                            onClick = {
                                permissionRequest()
                            }
                        ) {
                            Text(
                                text = "알림호출"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun permissionRequest(){
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.CAMERA)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            Toast.makeText(applicationContext, "모든 권한 허용됨", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePermissionDenied(permission: String) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            val message = when (permission) {
                android.Manifest.permission.POST_NOTIFICATIONS -> "푸시 알림 권한이 필요합니다."
                android.Manifest.permission.CAMERA -> "카메라 권한이 필요합니다."
                else -> "필수 권한이 필요합니다."
            }

            AlertDialog.Builder(this)
                .setTitle("권한 요청")
                .setMessage(message)
                .setPositiveButton("확인") { _, _ ->
                    permissionRequest()
                }
                .setNegativeButton("취소") { _, _ ->
                    Toast.makeText(applicationContext, "사용 불가", Toast.LENGTH_SHORT).show()
                }
                .show()
        } else {
            Toast.makeText(applicationContext, "설정에서 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
        }
    }
}
