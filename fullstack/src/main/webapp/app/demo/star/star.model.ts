import { BaseEntity } from './../../shared';

export class Star implements BaseEntity {
    constructor(
        public id?: number,
        public firstName?: string,
        public lastName?: string,
        public birthDate?: any,
        public birthLocation?: string,
    ) {
    }
}
