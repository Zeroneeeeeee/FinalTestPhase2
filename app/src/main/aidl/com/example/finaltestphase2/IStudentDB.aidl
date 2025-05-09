// IStudentDB.aidl
package com.example.finaltestphase2;

// Declare any non-default types here with import statements
import com.example.finaltestphase2.common.Student;
interface IStudentDB {
    List<Student> get100Students(int limit, int offset);
    List<Student> getTop10StudentByNameAndScore(String subjectName);
    List<Student> getTop10StudentsByScoreA(String city);
    List<Student> getTop10StudentsByScoreB(String city);
    List<Student> searchStudent(String name, String city);
    Student getStudentByID(int id);
}