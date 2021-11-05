/*
 * Copyright (c) 2021 Antoni Spaanderman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */




//      UNFINISHED      //




const fs = require('fs');
const { walk } = require('./recursiveList');

const assets = 'src/main/resources/assets';

walk(assets, (err, res) => {
	if (err) {
		throw err;
	}

	const l = process.cwd().length + 1;

	const files = {
		blockstates: [],
		models: [],
		textures: []
	};

	res.map(f => f.slice(l)).forEach(f => {
		const type = f.split('/')[5];
		if (files[type]) {
			const ns = f.split('/')[4];
			files[type].push({ ns, path: f });
		}
	});

	var modelsNeeded = [];

	files.blockstates.forEach(b => {
		const c = JSON.parse(fs.readFileSync(b.path).toString('utf-8'));
		modelsNeeded = modelsNeeded.concat(Object.values(c.variants).map(x => {
			const model = x.model.split(':');
			if (model.length == 1) {
				console.log('Model namespace not specified in ' + b);
				return ['minecraft', model[0]];
			} else if (model.length != 2) {
				console.log(`Unknown model "${x.model}" in ${b}`);
			} else {
				return model;
			}
		}));
	});

	files.models.forEach(m => {
		const model = m.path.slice(assets.length + m.ns.length + 9);
		const index = modelsNeeded.reduce((v, x, i) => x[0] == m.ns && x[1] + '.json' == model ? i : v, -1);

		if (index == -1) {
			console.log(`Model ${m.path} is not required by any blockstate`);
		} else {
			modelsNeeded = modelsNeeded.filter(x => x[0] != m.ns || x[1] + '.json' != model);
		}
	});

	modelsNeeded.filter(x => x).forEach(m => {
		console.log(`Model ${m[0]}:${m[1]} is required and not found`);
	});
});

// vim: set ts=4 sw=4 tw=0 noet :

