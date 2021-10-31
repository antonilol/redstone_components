const fs = require('fs');
const path = require('path');

const walk = function(dir, done) {
	var results = [];
	fs.readdir(dir, function(err, list) {
		if (err) return done(err);
		var pending = list.length;
		if (!pending) return done(null, results);
		list.forEach(function(file) {
			file = path.resolve(dir, file);
			fs.stat(file, function(err, stat) {
				if (stat && stat.isDirectory()) {
					walk(file, function(err, res) {
						results = results.concat(res);
						if (!--pending) done(null, results);
					});
				} else {
					results.push(file);
					if (!--pending) done(null, results);
				}
			});
		});
	});
}

walk('src', (err, res) => {
	if (err) {
		throw err;
	}

	const json = res.filter(f => f.endsWith('.json'));

	json.forEach(file => {
		const oldJSON = fs.readFileSync(file).toString('utf-8');
		const newJSON = JSON.stringify(JSON.parse(oldJSON), null, '\t') + '\n';

		fs.writeFileSync(file, newJSON);
	});
});

// vim: set ts=4 sw=4 tw=0 noet :

