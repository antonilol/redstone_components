#!/usr/bin/env node

/*
 * Copyright (c) 2021 - 2022 Antoni Spaanderman
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

const fs = require('fs');
const { walk } = require('./recursiveList');

const assets = 'src/main/resources/assets';

function color(...c) {
	return '\033[' + c.join(';') + 'm';
}

function warn(msg) {
	console.log(color(1, 33) + '(W) ' + msg + color(0));
}

function error(msg) {
	console.log(color(1, 31) + '(E) ' + msg + color(0));
}

function parseModel(b, x) {
	const model = (x.model || x).split(':');
	if (model.length == 1) {
		warn('Model namespace not specified in ' + b);
		return ['minecraft', model[0]];
	} else if (model.length != 2) {
		error(`Unknown model "${x.model}" in ${b}`);
	} else {
		return model;
	}
}

function parseTexture(b, x) {
	const t = x.split(':');
	if (t.length == 1) {
		warn('Texture namespace not specified in ' + b);
		return ['minecraft', t[0]];
	} else if (t.length != 2) {
		error(`Unknown texture "${x}" in ${b}`);
	} else {
		return t;
	}
}

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

	res
		.map(f => f.slice(l))
		.forEach(f => {
			const type = f.split('/')[5];
			if (files[type]) {
				const ns = f.split('/')[4];
				files[type].push({ ns, path: f });
			}
		});

	let modelsNeeded = [];

	files.blockstates.forEach(b => {
		let c;
		try {
			c = JSON.parse(fs.readFileSync(b.path).toString('utf-8'));
		} catch (e) {
			console.error(`Error while parsing ${b.path}:`, e);
			process.exit(1);
		}
		modelsNeeded.push(...Object.values(c.variants).map(parseModel.bind(null, b.path)));
	});

	let texturesNeeded = [];

	files.models.forEach(m => {
		let c;
		try {
			c = JSON.parse(fs.readFileSync(m.path).toString('utf-8'));
		} catch (e) {
			console.error(`Error while parsing ${m.path}:`, e);
			process.exit(1);
		}
		if (c.parent) {
			modelsNeeded.push(parseModel(m, c.parent));
		}

		if (c.textures) {
			texturesNeeded.push(
				...Object.values(c.textures)
					.filter(x => !x.startsWith('#'))
					.map(parseTexture.bind(null, m.path))
			);
		}
	});

	const unneededTextures = [];

	files.textures.forEach(t => {
		const tex = t.path.slice(assets.length + t.ns.length + 11);
		const index = texturesNeeded.reduce((v, x, i) => (x[0] == t.ns && x[1] + '.png' == tex ? i : v), -1);

		if (index == -1) {
			unneededTextures.push(tex);
		} else {
			texturesNeeded = texturesNeeded.filter(x => x[0] != t.ns || x[1] + '.png' != tex);
		}
	});

	const unneededModels = [];

	files.models.forEach(m => {
		const model = m.path.slice(assets.length + m.ns.length + 9);
		const index = modelsNeeded.reduce((v, x, i) => (x[0] == m.ns && x[1] + '.json' == model ? i : v), -1);

		if (index == -1) {
			if (model.startsWith('block')) {
				unneededModels.push(model);
			}
		} else {
			modelsNeeded = modelsNeeded.filter(x => x[0] != m.ns || x[1] + '.json' != model);
		}
	});

	// workaround for now. it is used directly from java code, not from any model
	delete unneededTextures[unneededTextures.indexOf('block/memory_cell_glow.png')];

	let ok = true;

	modelsNeeded
		.map(x => x.join(':'))
		.filter((x, i, l) => x && l.indexOf(x) == i)
		.forEach(m => {
			// how to check existance of 'minecraft:' models? skip for now
			if (m.split(':')[0] == 'minecraft') {
				return;
			}
			error(`Model ${m} is required and not found`);
			ok = false;
		});

	texturesNeeded
		.map(x => x.join(':'))
		.filter((x, i, l) => x && l.indexOf(x) == i)
		.forEach(t => {
			// how to check existance of 'minecraft:' textures? skip for now
			if (t.split(':')[0] == 'minecraft') {
				return;
			}
			error(`Texture ${t} is required and not found`);
			ok = false;
		});

	unneededModels.forEach(m => {
		warn(`Model ${m} is not required by any blockstate or model`);
		ok = false;
	});

	unneededTextures.forEach(t => {
		warn(`Texture ${t} is not required by any model`);
		ok = false;
	});

	if (ok) {
		console.log(color(1, 32) + 'Looks all good!' + color(0));
	} else {
		process.exit(1);
	}
});
