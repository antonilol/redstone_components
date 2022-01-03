#!/usr/bin/env node

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

const fs = require('fs');
const { walk } = require('./recursiveList');

function padRight(str, len, chr) {
	return (str + chr.repeat(len)).slice(0, len);
}

function stringifyModel(json, indent=0, pretty=1, face=0) {
	const nextFace = typeof json.faces === 'object';
	if (typeof json === 'object') {
		if (Array.isArray(json)) {
			if (json.length <= 8 && json.filter(x => typeof x === 'number').length) {
				return '[ ' + json.join(', ') + ' ]';
			}
			return '[\n' + '\t'.repeat(indent + 1) + json.map(x => stringifyModel(x, indent + 1)).join(',\n' + '\t'.repeat(indent + 1)) + '\n' + '\t'.repeat(indent) + ']';
		} else {
			const entries = Object.entries(json);
			if (face == 2) {
				return '{ ' + entries.map(x => JSON.stringify(x[0]) + ': ' + stringifyModel(x[1], 0, 0)).join(', ') + ' }';
			}
			return  (pretty ? '{\n' + '\t'.repeat(indent + 1) : '{ ') +
					entries.map(x => {
						const k = JSON.stringify(x[0]) + ': ';
						return (face || nextFace ? padRight(k, 8 + (x[0] == 'faces') + face, ' ') : k) + stringifyModel(x[1], indent + 1, pretty, face == 1 ? 2 : nextFace)
					}).join(pretty ? ',\n' + '\t'.repeat(indent + 1) : ', ') +
					(pretty ? '\n' + '\t'.repeat(indent) + '}' : ' }');
		}
	}
	return JSON.stringify(json);
}

walk('src', (err, res) => {
	if (err) {
		throw err;
	}

	const json = res.filter(f => f.endsWith('.json'));

	json.forEach(file => {
		const oldJSON = JSON.parse(fs.readFileSync(file).toString('utf-8'));
		var newJSON;
		if (Array.isArray(oldJSON.elements)) {
			newJSON = stringifyModel(oldJSON);
		} else {
			newJSON = JSON.stringify(oldJSON, null, '\t');
		}

		fs.writeFileSync(file, newJSON + '\n');
	});
});

// vim: set ts=4 sw=4 tw=0 noet :

