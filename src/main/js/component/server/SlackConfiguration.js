'use strict';
import React from 'react';
import PropTypes from 'prop-types';
import TextInput from '../../field/input/TextInput';
import ServerConfiguration from './ServerConfiguration';

import { alignCenter } from '../../../css/main.scss';
import { emptyLabel } from '../../../css/field.scss';
class SlackConfiguration extends ServerConfiguration {
	constructor(props) {
		super(props);
	}

	render() {
        let content =
            <div>
                <label className={emptyLabel}>No Slack server configuration required here.</label>
            </div>;
        return super.render(content);
	}
};

SlackConfiguration.propTypes = {
    headerText: PropTypes.string,
    configButtonTest: PropTypes.bool,
    configButtonsSave: PropTypes.bool,
    baseUrl: PropTypes.string,
    testUrl: PropTypes.string
};

SlackConfiguration.defaultProps = {
    headerText: 'Slack Configuration',
    configButtonsSave: false
};

export default SlackConfiguration;
