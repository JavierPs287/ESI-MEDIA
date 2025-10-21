// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html

module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma')
    ],
    client: {
      jasmine: {
        // Configuración de jasmine
        random: false, // Desactiva el orden aleatorio de tests
        seed: 42,
        stopSpecOnExpectationFailure: false
      },
      clearContext: false // deja visible el output de Jasmine Spec Runner en el navegador
    },
    jasmineHtmlReporter: {
      suppressAll: true // remueve los traces duplicados
    },
    coverageReporter: {
      dir: require('path').join(__dirname, './coverage/fe-esimedia'),
      subdir: '.',
      reporters: [
        { type: 'html' },
        { type: 'text-summary' },
        { type: 'lcovonly' }
      ],
      check: {
        global: {
          statements: 0,
          branches: 0,
          functions: 0,
          lines: 0
        }
      }
    },
    reporters: ['progress', 'kjhtml', 'coverage'],
    browsers: ['Chrome'],
    customLaunchers: {
      ChromeHeadlessCI: {
        base: 'ChromeHeadless',
        flags: [
          '--no-sandbox',
          '--disable-gpu',
          '--disable-dev-shm-usage',
          '--disable-software-rasterizer',
          '--disable-extensions'
        ]
      }
    },
    restartOnFileChange: true,
    singleRun: false,
    // Aumentar los timeouts para evitar que se cierre antes de generar reportes
    browserNoActivityTimeout: 60000,
    browserDisconnectTimeout: 10000,
    browserDisconnectTolerance: 3,
    captureTimeout: 210000,
    // Asegura que los reportes se generen antes de salir
    browserConsoleLogOptions: {
      terminal: true,
      level: 'log'
    }
  });
};
