import { Moment } from 'moment';

export interface IActor {
    id?: number;
    firstName?: string;
    lastName?: string;
    birthDate?: Moment;
    birthLocation?: string;
}

export class Actor implements IActor {
    constructor(
        public id?: number,
        public firstName?: string,
        public lastName?: string,
        public birthDate?: Moment,
        public birthLocation?: string
    ) {}
}
