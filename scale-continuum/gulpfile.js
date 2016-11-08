var gulp = require('gulp');
var browserSync = require('browser-sync').create();
var proxy = require('proxy-middleware');
var url = require('url');

gulp.task('watch', function () {
  gulp.watch('src/main/resources/public/**').on('change', browserSync.reload);
});

// use default task to launch Browsersync and watch JS files
gulp.task('serve', function () {
  var proxyOptions = url.parse('http://localhost:4567/api');
  proxyOptions.route = '/api';

  // Serve files from the root of this project
  browserSync.init({
    server: {
      baseDir: "src/main/resources/public",
      middleware: [proxy(proxyOptions)]
    }
  });

  gulp.start('watch');
});

gulp.task('default', ['serve']);
