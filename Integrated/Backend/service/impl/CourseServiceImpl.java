package com.team.savitha.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.team.savitha.dto.CourseDto;
import com.team.savitha.exception.CollegeNotFoundException;
import com.team.savitha.exception.CourseNotFoundException;
import com.team.savitha.mapper.CollegeMapper;
import com.team.savitha.mapper.CourseMapper;
import com.team.savitha.model.College;
import com.team.savitha.model.Course;
import com.team.savitha.repository.CollegeRepo;
import com.team.savitha.repository.CourseRepo;
import com.team.savitha.service.CourseService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CourseServiceImpl implements CourseService {
    
    private final CollegeRepo collegeRepo;

    private final CourseRepo courseRepo;

    @Override
    public CourseDto createCourse(CourseDto courseDto) {
        Course course = CourseMapper.mapToCourse(courseDto);
        Course savedCourse = courseRepo.save(course);
        return CourseMapper.mapToCourseDto(savedCourse);
    }

    @Override
    public CourseDto getCourseById(Long courseId) {
        Optional<Course> optionalCourse = courseRepo.findById(courseId);
        Course course = optionalCourse.orElseThrow(() ->
                new CourseNotFoundException("Course not found with id: " + courseId));
        return CourseMapper.mapToCourseDto(course);
    }

    @Override
    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepo.findAll();
        return courses.stream()
                .map(CourseMapper::mapToCourseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CourseDto updateCourse(Long courseId, CourseDto courseDto) {
        Optional<Course> optionalCourse = courseRepo.findById(courseId);
        Course existingCourse = optionalCourse.orElseThrow(() ->
                new CourseNotFoundException("Course not found with id: " + courseId));

        // Update fields with values from CourseDto
        existingCourse.setCourseName(courseDto.getCourseName());
        existingCourse.setCourseDescription(courseDto.getCourseDescription());
        existingCourse.setCourseDuration(courseDto.getCourseDuration());
        existingCourse.setFees(courseDto.getFees());
        existingCourse.setNoOfSeats(courseDto.getNoOfSeats());

        // Assuming you have a method to update the institute in the Course entity
        // You can map the instituteDto to Institute and set it in the existingCourse
        // College institute = CollegeMapper.mapToCollege(courseDto.getInstituteDto());
        // existingCourse.setInstitute(institute);

        // Save the updated entity back to the repository
        Course updatedCourse = courseRepo.save(existingCourse);
        return CourseMapper.mapToCourseDto(updatedCourse);
    }

    @Override
    public void deleteCourse(Long courseId) {
        courseRepo.deleteById(courseId);
    }
    @Override
    public List<CourseDto> getCoursesByInstituteId(Long instituteId) {
        Optional<College> optionalCollege = collegeRepo.findById(instituteId);
        College college = optionalCollege.orElseThrow(() ->
                new CollegeNotFoundException("College not found with id: " + instituteId));

        List<Course> courses = college.getCourses();
        return courses.stream()
                .map(CourseMapper::mapToCourseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public CourseDto createCourse(Long instituteId, CourseDto courseDto) {
        Optional<College> optionalCollege = collegeRepo.findById(instituteId);
        College college = optionalCollege.orElseThrow(() ->
                new CollegeNotFoundException("College not found with id: " + instituteId));

        Course course = CourseMapper.mapToCourse(courseDto);
        // Associate the course with the college
        course.setCollege(college);

        Course savedCourse = courseRepo.save(course);
        // Add the course to the list of courses for the college
        college.getCourses().add(savedCourse);

        // Save the updated college back to the repository
        collegeRepo.save(college);

        return CourseMapper.mapToCourseDto(savedCourse);
    }

}
