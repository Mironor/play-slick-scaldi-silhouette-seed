var gulp = require('gulp'),
    concat = require('gulp-concat'),
    rename = require('gulp-rename'),
    less = require('gulp-less');

var paths = {
    styles : 'public/styles/less/main.less'
};

gulp.task('deps_dev', function () {
    gulp.src([
        'bower_dependencies/angular/angular.js',
        'bower_dependencies/angular-ui-router/release/angular-ui-router.js',
        'bower_dependencies/angular-translate/angular-translate.js',
        'bower_dependencies/angular-recursion/angular-recursion.js',
        'bower_dependencies/ng-file-upload/angular-file-upload.js',
        'bower_dependencies/lodash/dist/lodash.js',
        'bower_dependencies/underscore.string/lib/underscore.string.js'
    ])
        .pipe(concat('vendor.js'))
        .pipe(gulp.dest('public/js/'))
});

gulp.task('deps_test', function () {
    gulp.src([
        'bower_dependencies/angular/angular.js',
        'bower_dependencies/angular-mocks/angular-mocks.js',
        'bower_dependencies/angular-translate/angular-translate.js',
        'bower_dependencies/angular-ui-router/release/angular-ui-router.js',
        'bower_dependencies/angular-recursion/angular-recursion.js',
        'bower_dependencies/ng-file-upload/angular-file-upload.js',
        'bower_dependencies/lodash/dist/lodash.js',
        'bower_dependencies/underscore.string/lib/underscore.string.js',

        'bower_dependencies/jquery/dist/jquery.js',
        'bower_dependencies/jasmine-jquery/lib/jasmine-jquery.js'
    ])
        .pipe(concat('vendor-test.js'))
        .pipe(gulp.dest('public/js/'))
});

gulp.task('less', function () {
    gulp.src(paths.styles)
        .pipe(less())
        .pipe(rename('main.css'))
        .pipe(gulp.dest('public/styles/'))
});

gulp.task('watch', function () {
    gulp.watch(paths.styles, ['less'])
});