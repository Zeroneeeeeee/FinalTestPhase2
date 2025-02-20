package com.example.finaltestphase2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finaltestphase2.database.DatabaseHelper
import com.example.finaltestphase2.model.Student
import com.example.finaltestphase2.ui.theme.FinalTestPhase2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load JSON và chèn dữ liệu vào database khi app chạy
        JsonUtils.parseJsonAndInsertToDB(this)

        setContent {
            val students = remember { mutableStateOf(listOf<Student>()) }
            val dbHelper = DatabaseHelper(this)

            StudentListScreen(
                students.value,
                onLimitChange = { limit -> students.value = dbHelper.getStudentsLimit(limit) },
                onTop10Click = { students.value = dbHelper.getTop10StudentsByScore() }
            )
        }
    }
}

@Composable
fun StudentListScreen(students: List<Student>, onLimitChange: (Int) -> Unit,onTop10Click: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Các nút bấm để hiển thị số lượng sinh viên khác nhau
        Column (
            modifier = Modifier.fillMaxWidth()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Button(onClick = { onLimitChange(100) }) {
                Text("Hiển thị 100 học sinh")
            }
            Button(onClick = { onTop10Click() }) {
                Text("10 học sinh có điểm SUM khối A cao nhất")
            }
            Button(onClick = { onLimitChange(20) }) {
                Text("Hiển thị 20 học sinh")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (students.isEmpty()) {
            // Hiển thị thông báo khi chưa có dữ liệu
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Đang tải dữ liệu...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // Hiển thị danh sách sinh viên
            LazyColumn {
                items(students) { student ->
                    StudentItem(student)
                }
            }
        }
    }
}

@Composable
fun StudentItem(student: Student) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(text = "ID: ${student.studentId}", fontWeight = FontWeight.Bold)
        Text(text = "Họ và tên: ${student.firstName} ${student.lastName}")
        Text(text = "Ngày sinh: ${student.dateOfBirth}")
        Text(text = "Thành phố: ${student.city}")
        Text(text = "SĐT: ${student.phone}")

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Môn học:", fontWeight = FontWeight.Bold)

        student.subjects.forEach { subject ->
            Text(text = "${subject.name}: ${subject.score}", modifier = Modifier.padding(start = 8.dp))
        }
    }
}