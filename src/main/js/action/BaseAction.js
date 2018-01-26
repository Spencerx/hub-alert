class BaseAction {
    constructor(props) {

    }

    handleSetState(name, value) {
        const { stateHandler } = this.props;
        // the component should pass a reference to the setState function
		stateHandler({
			[name]: value
		});
	}
}

export default BaseAction;
