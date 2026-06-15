// ДАН-login frontend build — TARA-Login-ийн disain/gulpfile.js бүтцийн дагуу.
// SCSS → static/styles/main.css, JS → static/scripts/main.js, assets → static/assets/

const { src, dest, series, watch } = require('gulp');
const sass = require('gulp-sass')(require('sass'));
const autoprefixerModule = require('gulp-autoprefixer');
const autoprefixer = autoprefixerModule.default || autoprefixerModule;
const concat = require('gulp-concat');
const uglify = require('gulp-uglify-es').default;
const sourcemaps = require('gulp-sourcemaps');

const OUT = '../src/main/resources/static';

// SCSS → compressed, autoprefixed main.css (+ sourcemap)
function buildCss() {
  return src('styles/main.scss')
    .pipe(sourcemaps.init())
    .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
    .pipe(autoprefixer())
    .pipe(sourcemaps.write('.'))
    .pipe(dest(OUT + '/styles'));
}

// JS → concat + uglify (ES6) → main.js (+ sourcemap)
function buildJs() {
  return src('scripts/**/*.js')
    .pipe(sourcemaps.init())
    .pipe(concat('main.js'))
    .pipe(uglify())
    .pipe(sourcemaps.write('.'))
    .pipe(dest(OUT + '/scripts'));
}

// Зураг / SVG → static/assets/ (хэвээр хуулна)
function buildAssets() {
  return src('assets/**/*', { encoding: false }).pipe(dest(OUT + '/assets'));
}

const build = series(buildCss, buildJs, buildAssets);

function watchTask() {
  watch('styles/**/*.scss', buildCss);
  watch('scripts/**/*.js', buildJs);
  watch('assets/**/*', buildAssets);
}

exports['build:css'] = buildCss;
exports['build:js'] = buildJs;
exports['build:assets'] = buildAssets;
exports.build = build;
exports.watch = series(build, watchTask);
exports.default = build;
