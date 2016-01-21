/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

'use strict';

var gulp = require('gulp');
var gulpif = require('gulp-if');
var rename = require('gulp-rename');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var jshint = require('gulp-jshint');
var less = require('gulp-less');
var autoprefixer = require('gulp-autoprefixer');
var sourcemaps = require('gulp-sourcemaps');
var replace = require('gulp-replace');
var connect = require('gulp-connect');
var minimist = require('minimist');

// Source folder
var srcRoot = 'src/main/app/';
// Target folder
var targetRoot = 'target/app/';
// REST server for dev
var restServerRoot = 'http://localhost:8888/rest/';

// Modify this list to include or exclude JS you want
var jsFileList = [
    'bower_components/angular/angular.js',
    'bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
    'bower_components/angular-resource/angular-resource.js',
    'bower_components/angular-route/angular-route.js',
    'bower_components/marked/marked.min.js',
    srcRoot + 'javascript/*.js'
];

var flags = minimist(process.argv.slice(2));
var isProduction = flags.production || flags.prod || false;
var isWatching = flags.watch || false;
var isServing = flags.serve || false;

// Lint our javascript files
gulp.task('lint', function() {
    return gulp.src([srcRoot + 'javascript/*.js', 'gulpfile.js'])
        .pipe(jshint())
        .pipe(jshint.reporter('default'))
        .pipe(jshint.reporter('fail'));
});

// Compile all require javascript files and copy it to the target folder
gulp.task('js', ['lint'], function() {
    return gulp.src(jsFileList)
        .pipe(concat('public.js'))
        .pipe(replace(/@@rest-server-root@@/g, gulpif(isProduction, '/', restServerRoot)))
        .pipe(gulpif(isProduction, uglify()))
        .pipe(gulpif(isProduction, rename({ extname: '.min.js' })))
        .pipe(gulp.dest(targetRoot + 'js'));
});

// Compile all require LESS files and copy it to the target folder
gulp.task('less', ['font'], function() {
    return gulp.src(srcRoot + 'less/public.less')
        .pipe(gulpif(isProduction, sourcemaps.init()))
        .pipe(less())
        .pipe(autoprefixer({
            browsers: ['last 2 versions', 'ie 8', 'ie 9', 'android 2.3', 'android 4', 'opera 12']
        }))
        .pipe(gulpif(isProduction, sourcemaps.write()))
        .pipe(gulpif(isProduction, rename({ extname: '.min.css' })))
        .pipe(gulp.dest(targetRoot + 'css'));
});

// Copy fonts to the target folder
gulp.task('font', function() {
    return gulp.src('bower_components/font-awesome/fonts/*')
        .pipe(gulp.dest(targetRoot + 'font'));
});

// Copy images to the target folder
gulp.task('img', function() {
    return gulp.src(srcRoot + 'img/*')
        .pipe(gulp.dest(targetRoot + 'img'));
});

// Copy partial HTML files to the target folder
gulp.task('partial', function() {
    return gulp.src(srcRoot + 'partial/*')
        .pipe(gulp.dest(targetRoot + 'partial'));
});

// Compile index.html and copy it to the target folder
gulp.task('index', function() {
    return gulp.src(srcRoot + 'index.html')
        .pipe(replace(/@@public-(css|js)@@/g, gulpif(isProduction, 'public.min.$1', 'public.$1')))
        .pipe(gulp.dest(targetRoot));
});

// Watch changes within our sources and launch tasks accordingly
gulp.task('watch', function() {
    gulp.watch([srcRoot + 'javascript/*', 'gulpfile.js'], ['js']);
    gulp.watch(srcRoot + 'less/*', ['less']);
    gulp.watch(srcRoot + 'img/*', ['img']);
    gulp.watch(srcRoot + 'partial/*', ['partial']);
    gulp.watch(srcRoot + 'index/*', ['index']);
});

// Start a local webserver
gulp.task('webserver', function() {
    connect.server({
        root: targetRoot,
        port: 8000,
        livereload: true
    });
});

// Build the app
gulp.task('build', ['js', 'less', 'img', 'partial', 'index'], function() {
    console.log('✔ Build done!');

    if (isWatching) {
        console.log('=> Watching sources...');
        gulp.start('watch');
    }
    if (isServing) {
        console.log('=> Launching webserver...');
        gulp.start('webserver');
    }
});
