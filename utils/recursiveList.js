const fs = require('fs');
const path = require('path');

// credits to https://stackoverflow.com/a/5827895/13800918
const walk = function (dir, done) {
	const results = [];
	fs.readdir(dir, function (err, list) {
		if (err) return done(err);
		let pending = list.length;
		if (!pending) return done(null, results);
		list.forEach(function (file) {
			file = path.resolve(dir, file);
			fs.stat(file, function (err, stat) {
				if (stat && stat.isDirectory()) {
					walk(file, function (err, res) {
						results.push(...res);
						if (!--pending) done(null, results);
					});
				} else {
					results.push(file);
					if (!--pending) done(null, results);
				}
			});
		});
	});
};

module.exports = { walk };
